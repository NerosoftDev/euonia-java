package com.euonia.reflection;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * TypeHelper is a utility class that provides methods for coercing values to a
 * desired type, handling primitive types, enums, date/time conversions,
 * collections, and more.
 * It is designed to simplify type conversions and ensure type safety in various
 * scenarios.
 */
public final class TypeHelper {
    private TypeHelper() {
    }

    public static Object coerceValue(Class<?> desiredType, Class<?> valueType, Object value) {
        if (desiredType == null)
            throw new IllegalArgumentException("desiredType is null");

        if (value == null) {
            if (desiredType.isPrimitive()) {
                return defaultPrimitiveValue(desiredType);
            }
            return null;
        }

        if (valueType == null)
            valueType = value.getClass();

        if (desiredType.isAssignableFrom(valueType)) {
            return value;
        }

        Class<?> boxedDesired = boxIfPrimitive(desiredType);
        Class<?> boxedValue = boxIfPrimitive(valueType);

        // Enums
        if (boxedDesired.isEnum()) {
            return convertToEnum(boxedDesired, value);
        }

        // Date/time targets
        if (isDateTimeTarget(boxedDesired)) {
            return convertToDateTime(boxedDesired, value);
        }

        // Collections/Map/JSON
        if (Collection.class.isAssignableFrom(boxedDesired) || boxedDesired.isArray()
                || Map.class.isAssignableFrom(boxedDesired)) {
            return convertToCollectionOrMap(boxedDesired, value);
        }

        // String target
        if (boxedDesired == String.class) {
            return value.toString();
        }

        // Boolean target
        if (boxedDesired == Boolean.class) {
            return convertToBoolean(value);
        }

        // Numeric
        if (Number.class.isAssignableFrom(boxedDesired) || isPrimitiveNumber(desiredType)) {
            return convertToNumber(boxedDesired, value);
        }

        if (boxedDesired == UUID.class) {
            return convertToUUID(value);
        }

        if (boxedDesired == Character.class) {
            return convertToCharacter(value);
        }

        if (boxedDesired.isAssignableFrom(boxedValue)) {
            return value;
        }

        // As a last attempt, try Jackson convert if present
        Object conv = tryJacksonConvert(value, desiredType);
        if (conv != null)
            return conv;

        throw new IllegalArgumentException(String.format("Cannot convert value of type %s to %s (value=%s)",
                value.getClass().getName(), desiredType.getName(), value));
    }

    @SuppressWarnings("unchecked")
    public static <T> T coerceValue(Class<T> desiredType, Object value) {
        return (T) coerceValue(desiredType, (value == null ? null : value.getClass()), value);
    }

    /**
     * If the given class is a primitive type, returns its boxed type. Otherwise,
     * returns the class itself.
     *
     * @param type the class to check
     * @return the boxed type if the class is primitive, otherwise the class itself
     */
    public static Class<?> boxIfPrimitive(Class<?> type) {
        if (!type.isPrimitive()) {
            return type;
        }
        if (type == int.class) {
            return Integer.class;
        }
        if (type == long.class) {
            return Long.class;
        }
        if (type == short.class) {
            return Short.class;
        }
        if (type == byte.class) {
            return Byte.class;
        }
        if (type == float.class) {
            return Float.class;
        }
        if (type == double.class) {
            return Double.class;
        }
        if (type == boolean.class) {
            return Boolean.class;
        }
        if (type == char.class) {
            return Character.class;
        }
        return type;
    }

    public static boolean isPrimitiveNumber(Class<?> type) {
        return type == int.class || type == long.class || type == short.class || type == byte.class
                || type == float.class || type == double.class;
    }

    public static Object defaultPrimitiveValue(Class<?> primitiveType) {
        if (primitiveType == boolean.class)
            return false;
        if (primitiveType == char.class)
            return '\0';
        if (primitiveType == byte.class)
            return (byte) 0;
        if (primitiveType == short.class)
            return (short) 0;
        if (primitiveType == int.class)
            return 0;
        if (primitiveType == long.class)
            return 0L;
        if (primitiveType == float.class)
            return 0.0f;
        if (primitiveType == double.class)
            return 0.0d;
        return null;
    }

    @SuppressWarnings({ "rawtypes", "unchecked", "IfCanBeSwitch" })
    private static Object convertToEnum(Class<?> enumType, Object value) {

        if (value == null) {
            return null;
        }

        if (value instanceof Enum) {
            return value;
        }

        if (value instanceof String target) {
            String string = target.trim();
            if (string.isEmpty()) {
                return null;
            }
            try {
                return Enum.valueOf((Class<? extends Enum>) enumType, string);
            } catch (IllegalArgumentException ex) {
                // Try ordinal
                try {
                    int ord = Integer.parseInt(string);
                    Enum[] constants = (Enum[]) enumType.getEnumConstants();
                    if (ord >= 0 && ord < constants.length)
                        return constants[ord];
                } catch (NumberFormatException ignored) {
                }
                throw ex;
            }
        }

        if (value instanceof Number number) {
            int ord = number.intValue();
            Enum[] constants = (Enum[]) enumType.getEnumConstants();
            if (ord >= 0 && ord < constants.length) {
                return constants[ord];
            }
            throw new IllegalArgumentException("Enum ordinal out of range: " + ord);
        }

        String vs = value.toString();
        if (vs != null && !vs.isEmpty()) {
            return convertToEnum(enumType, vs);
        }
        throw new IllegalArgumentException("Cannot convert to enum: " + value);
    }

    private static Object convertToBoolean(Object value) {
        if (value instanceof Boolean)
            return value;
        if (value instanceof Number) {
            return ((Number) value).intValue() != 0;
        }
        String s = value.toString().trim().toLowerCase(Locale.ROOT);
        return switch (s) {
            case "", "false", "0", "no", "n" -> false;
            case "true", "1", "yes", "y" -> true;
            default -> throw new IllegalArgumentException("Cannot convert to boolean: " + value);
        };
    }

    private static Object convertToNumber(Class<?> targetNumberClass, Object value) {
        if (value instanceof Number) {
            return castNumber((Number) value, targetNumberClass);
        }
        String s = value.toString().trim();
        if (s.isEmpty()) {
            return castNumber(BigDecimal.ZERO, targetNumberClass);
        }
        try {
            BigDecimal bd = new BigDecimal(s);
            return castNumber(bd, targetNumberClass);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cannot convert to number: " + value, ex);
        }
    }

    private static Object castNumber(Number number, Class<?> targetNumberClass) {
        if (targetNumberClass == Byte.class)
            return number.byteValue();
        if (targetNumberClass == Short.class)
            return number.shortValue();
        if (targetNumberClass == Integer.class)
            return number.intValue();
        if (targetNumberClass == Long.class)
            return number.longValue();
        if (targetNumberClass == Float.class)
            return number.floatValue();
        if (targetNumberClass == Double.class)
            return number.doubleValue();
        if (targetNumberClass == BigDecimal.class) {
            if (number instanceof BigDecimal)
                return number;
            return BigDecimal.valueOf(number.doubleValue());
        }
        if (targetNumberClass.isInstance(number))
            return number;
        throw new IllegalArgumentException("Unsupported target numeric type: " + targetNumberClass.getName());
    }

    private static Object convertToUUID(Object value) {
        if (value instanceof UUID) {
            return value;
        }
        String s = value.toString().trim();
        if (s.isEmpty())
            return null;
        return UUID.fromString(s);
    }

    private static Object convertToCharacter(Object value) {

        if (value == null) {
            return '\0';
        }
        if (value instanceof Character) {
            return value;
        }
        String s = value.toString();
        if (s.isEmpty())
            return '\0';
        return s.charAt(0);
    }

    // Extended conversions: date/time, collections, maps, JSON (when Jackson is
    // available)
    private static boolean isDateTimeTarget(Class<?> boxedDesired) {
        return boxedDesired == Date.class || boxedDesired == Instant.class || boxedDesired == LocalDateTime.class
                || boxedDesired == LocalDate.class || boxedDesired == LocalTime.class
                || boxedDesired == OffsetDateTime.class
                || boxedDesired == ZonedDateTime.class;
    }

    private static Object convertToDateTime(Class<?> target, Object value) {
        if (value == null)
            return null;

        if (target.isInstance(value))
            return value;

        if (target == Date.class) {
            if (value instanceof Date)
                return value;
            if (value instanceof Number)
                return new Date(((Number) value).longValue());
            String s = value.toString().trim();
            if (s.isEmpty())
                return null;
            try {
                Instant inst = Instant.parse(s);
                return Date.from(inst);
            } catch (DateTimeParseException ignored) {
            }
            try {
                long l = Long.parseLong(s);
                return new Date(l);
            } catch (NumberFormatException ignored) {
            }
            throw new IllegalArgumentException("Cannot convert to Date: " + value);
        }

        if (Temporal.class.isAssignableFrom(target) || target == LocalDate.class || target == LocalDateTime.class
                || target == LocalTime.class || target == Instant.class || target == OffsetDateTime.class
                || target == ZonedDateTime.class) {
            if (value instanceof Number) {
                long epoch = ((Number) value).longValue();
                Instant inst = Instant.ofEpochMilli(epoch);
                return convertInstantToTarget(target, inst);
            }

            String s = value.toString().trim();
            if (s.isEmpty())
                return null;
            List<java.util.function.Function<String, Object>> parsers = Arrays.asList(
                    Instant::parse,
                    OffsetDateTime::parse,
                    ZonedDateTime::parse,
                    LocalDateTime::parse,
                    LocalDate::parse,
                    LocalTime::parse);
            for (var p : parsers) {
                try {
                    Object parsed = p.apply(s);

                    return switch (target.getSimpleName()) {
                        case "Instant" -> convertInstantToTarget(target, (Instant) parsed);
                        case "OffsetDateTime" -> convertInstantToTarget(target, ((OffsetDateTime) parsed).toInstant());
                        case "ZonedDateTime" -> convertInstantToTarget(target, ((ZonedDateTime) parsed).toInstant());
                        case "LocalDateTime" -> convertInstantToTarget(target,
                                ((LocalDateTime) parsed).atZone(ZoneId.systemDefault()).toInstant());
                        case "LocalDate" -> convertInstantToTarget(target,
                                ((LocalDate) parsed).atStartOfDay(ZoneId.systemDefault()).toInstant());
                        case "LocalTime" -> convertInstantToTarget(target, LocalDateTime
                                .of(LocalDate.now(), (LocalTime) parsed).atZone(ZoneId.systemDefault()).toInstant());
                        default -> null;
                    };
                } catch (DateTimeParseException ignored) {
                }
            }

            try {
                long l = Long.parseLong(s);
                Instant inst = Instant.ofEpochMilli(l);
                return convertInstantToTarget(target, inst);
            } catch (NumberFormatException ignored) {
            }

            throw new IllegalArgumentException("Cannot parse date/time value: " + value);
        }

        throw new IllegalArgumentException("Unsupported date/time target: " + target.getName());
    }

    private static Object convertInstantToTarget(Class<?> target, Instant inst) {
        if (target == Instant.class)
            return inst;
        if (target == LocalDateTime.class)
            return LocalDateTime.ofInstant(inst, ZoneId.systemDefault());
        if (target == LocalDate.class)
            return LocalDateTime.ofInstant(inst, ZoneId.systemDefault()).toLocalDate();
        if (target == LocalTime.class)
            return LocalDateTime.ofInstant(inst, ZoneId.systemDefault()).toLocalTime();
        if (target == OffsetDateTime.class)
            return OffsetDateTime.ofInstant(inst, ZoneId.systemDefault());
        if (target == ZonedDateTime.class)
            return ZonedDateTime.ofInstant(inst, ZoneId.systemDefault());
        if (target == Date.class)
            return Date.from(inst);
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Object convertToCollectionOrMap(Class<?> desiredType, Object value) {
        if (value == null)
            return null;
        if (desiredType.isInstance(value))
            return value;

        if (value instanceof Map) {
            Object conv = tryJacksonConvert(value, desiredType);
            if (conv != null)
                return conv;
            if (Map.class.isAssignableFrom(desiredType))
                return value;
        }

        if (value instanceof Iterable || value.getClass().isArray()) {
            List<Object> src = new ArrayList<>();
            if (value instanceof Iterable)
                for (Object o : (Iterable<?>) value)
                    src.add(o);
            else {
                int len = Array.getLength(value);
                for (int i = 0; i < len; i++)
                    src.add(Array.get(value, i));
            }

            if (desiredType.isArray()) {
                Class<?> comp = desiredType.getComponentType();
                Object arr = Array.newInstance(comp, src.size());
                for (int i = 0; i < src.size(); i++) {
                    Array.set(arr, i, coerceValue(comp, src.get(i) == null ? null : src.get(i).getClass(), src.get(i)));
                }
                return arr;
            }

            if (Collection.class.isAssignableFrom(desiredType)) {
                try {
                    Collection<Object> coll = (Collection<Object>) desiredType.getDeclaredConstructor().newInstance();
                    coll.addAll(src);
                    return coll;
                } catch (Exception ex) {
                    return src;
                }
            }
        }

        if (value instanceof String string) {
            String s = string.trim();
            if (s.startsWith("[") || s.startsWith("{")) {
                Object parsed = tryJacksonParse(string);
                if (parsed != null) {
                    if (desiredType.isInstance(parsed))
                        return parsed;
                    Object conv = tryJacksonConvert(parsed, desiredType);
                    if (conv != null)
                        return conv;
                }
            }
        }

        if (desiredType == Map.class && value instanceof String) {
            Object parsed = tryJacksonParse((String) value);
            if (parsed instanceof Map)
                return parsed;
        }

        throw new IllegalArgumentException("Cannot convert to collection/map target: " + desiredType.getName());
    }

    // Reflection-based optional Jackson integration: parse JSON string to Object
    // using ObjectMapper
    private static Object tryJacksonParse(String json) {
        try {
            Class<?> mapperClass = Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
            Object mapper = mapperClass.getDeclaredConstructor().newInstance();
            java.lang.reflect.Method read = mapperClass.getMethod("readValue", String.class, Class.class);
            return read.invoke(mapper, json, Object.class);
        } catch (Exception ex) {
            return null;
        }
    }

    private static Object tryJacksonConvert(Object value, Class<?> desiredType) {
        try {
            Class<?> mapperClass = Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
            Object mapper = mapperClass.getDeclaredConstructor().newInstance();
            java.lang.reflect.Method convert = mapperClass.getMethod("convertValue", Object.class, Class.class);
            return convert.invoke(mapper, value, desiredType);
        } catch (Exception ex) {
            return null;
        }
    }
}
