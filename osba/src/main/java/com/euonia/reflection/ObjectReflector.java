package com.euonia.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

public class ObjectReflector {

    private static final ConcurrentMap<String, Method> factoryMethodCache = new java.util.concurrent.ConcurrentHashMap<>();

    public static <T, A extends Annotation> Method findFactoryMethod(Class<T> targetType, Class<A> annotationType,
                                                                     Object[] criteria) {
        var name = getMethodName(targetType, annotationType, criteria);
        return factoryMethodCache.computeIfAbsent(name, _ -> findMatchedMethod(targetType, annotationType, criteria));
    }

    private static <T> String getMethodName(Class<T> targetType, Class<? extends Annotation> annotationType,
                                            Object[] criteria) {
        var typeName = targetType.getName();
        var annotationName = annotationType.getSimpleName();
        var parameterNames = getParameterTypeName(criteria);
        return typeName + "." + annotationName + "(" + parameterNames + ")";
    }

    @SuppressWarnings("SequencedCollectionMethodCanBeUsed")
    private static <T> Method findMatchedMethod(Class<T> targetType, Class<? extends Annotation> annotationType,
                                                Object[] criteria) {
        var candidates = getCandidateMethods(targetType, annotationType, 0);
        if (candidates.isEmpty()) {
            var names = getConventionMethodNames(annotationType);
            throw new MissingMethodException(targetType.getTypeName(), String.join("/", names));
        }

        var matches = new ArrayList<Map.Entry<Method, Integer>>();

        int parameterCount;

        if (criteria != null) {
            parameterCount = criteria.getClass() == Object[].class ? criteria.length : 1;
        } else {
            parameterCount = 1;
        }

        if (parameterCount > 0) {
            for (var entry : candidates.entrySet()) {
                var score = 0;
                var methodParameters = entry.getKey().getParameters();
                if (methodParameters.length != parameterCount) {
                    continue;
                }

                var index = 0;
                assert criteria != null;
                if (criteria.getClass() == Object[].class) {
                    for (var c : criteria) {
                        var currentScore = calculateParameterMatchScore(methodParameters[index], c);
                        if (currentScore == 0) {
                            break;
                        }
                        score += currentScore;
                        index++;
                    }
                } else {
                    var currentScore = calculateParameterMatchScore(methodParameters[index], criteria);
                    if (currentScore > 0) {
                        score += currentScore;
                        index++;
                    }
                }
                if (index == parameterCount) {
                    matches.add(new AbstractMap.SimpleEntry<>(entry.getKey(), score + entry.getValue()));
                }
            }
        } else {
            for (var entry : candidates.entrySet()) {
                var method = entry.getKey();
                if (method.getParameterCount() == 0) {
                    matches.add(new AbstractMap.SimpleEntry<>(method, entry.getValue()));
                }
            }
        }

        if (matches.isEmpty()) {
            for (var entry : candidates.entrySet()) {
                var method = entry.getKey();
                var lastParameter = method.getParameters()[method.getParameterCount() - 1];
                if (lastParameter != null && lastParameter.isVarArgs()) {
                    matches.add(new AbstractMap.SimpleEntry<>(method, entry.getValue() + 1));
                }
            }
        }

        if (matches.isEmpty()) {
            var names = getConventionMethodNames(annotationType);
            var parameterNames = getParameterTypeName(criteria);
            throw new MissingMethodException(targetType.getTypeName(),
                String.join("/", names) + "(" + parameterNames + ")");
        }

        if (matches.size() > 1) {

            var maxScore = matches.stream().map(Map.Entry::getValue)
                                  .max(Integer::compareTo)
                                  .orElse(0);
            var topMatches = matches.stream().filter(m -> Objects.equals(m.getValue(), maxScore)).toList();
            if (topMatches.size() > 1) {
                throw new AmbiguousMethodException("Multiple methods were found for the specified operation");
            }

            return topMatches.get(0).getKey();
        } else {
            return matches.get(0).getKey();
        }
    }

    private static int calculateParameterMatchScore(Parameter parameter, Object criteria) {
        if (criteria == null) {

            var parameterType = parameter.getType();

            if (parameterType.isPrimitive()) {
                return 0;
            }

            if (parameterType == Object.class) {
                return 2;
            }

            if (parameterType == Object[].class) {
                return 2;
            }

            if (parameterType.isLocalClass()) {
                return 1;
            }

            if (parameterType.isArray()) {
                return 1;
            }

            if (parameterType.isInterface()) {
                return 1;
            }

        } else {
            if (criteria.getClass() == parameter.getType()) {
                return 3;
            }

            switch (criteria) {
                case Long _ when parameter.getType() == long.class -> {
                    return 3;
                }
                case Double _ when parameter.getType() == double.class -> {
                    return 3;
                }
                case Float _ when parameter.getType() == float.class -> {
                    return 3;
                }
                case Integer _ when parameter.getType() == int.class -> {
                    return 3;
                }
                case Boolean _ when parameter.getType() == boolean.class -> {
                    return 3;
                }
                default -> {
                }
            }

            var parameterType = parameter.getType();

            if (parameterType == Object.class) {
                return 1;
            }

            if (parameterType.isInstance(criteria)) {
                return 2;
            }
        }

        return 0;
    }

    private static Map<Method, Integer> getCandidateMethods(Class<?> targetType,
                                                            Class<? extends Annotation> annotationType, int level) {
        var validNames = getConventionMethodNames(annotationType);
        var result = new HashMap<Method, Integer>();

        var methods = Arrays.stream(targetType.getDeclaredMethods())
                            .filter(method -> method.isAnnotationPresent(annotationType) || validNames.contains(method.getName()))
                            .toList();

        for (var method : methods) {
            result.put(method, level);
        }

        if (result.isEmpty() && targetType.getSuperclass() != Object.class
            && !targetType.getSuperclass().isInterface()) {
            result.putAll(getCandidateMethods(targetType.getSuperclass(), annotationType, level - 1));
        }
        return result;
    }

    private static String getParameterTypeName(Object[] criteria) {
        if (criteria == null) {
            return "";
        }

        var parameterTypeNames = new ArrayList<String>();
        if (criteria.getClass() == Object[].class) {
            for (var c : criteria) {
                parameterTypeNames.add(c == null ? "null" : c.getClass().getTypeName());
            }
        } else {
            parameterTypeNames.add(criteria.getClass().getTypeName());
        }
        return String.join(", ", parameterTypeNames);
    }

    private static List<String> getConventionMethodNames(Class<? extends Annotation> annotationType) {
        var names = new ArrayList<String>();

        var validNames = List.of(annotationType.getSimpleName(), annotationType.getSimpleName().replace("Factory", ""));

        for (var method : validNames) {
            var firstChar = method.charAt(0);
            var name = Character.toLowerCase(firstChar) + method.substring(1);
            if (!names.contains(name)) {
                names.add(name);
            }
        }
        return names;
    }
}
