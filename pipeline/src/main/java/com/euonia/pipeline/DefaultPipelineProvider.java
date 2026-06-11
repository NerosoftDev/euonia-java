package com.euonia.pipeline;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.euonia.reflection.ServiceProvider;

public class DefaultPipelineProvider extends PipelineBase {
    private static final String HANDLE_METHOD_NAME = "handle";
    private static final String HANDLE_METHOD_NAME_ASYNC = "handleAsync";

    private final ServiceProvider resolver;

    public DefaultPipelineProvider(ServiceProvider resolver) {
        this.resolver = resolver;
    }

    @Override
    protected PipelineDelegate getNext(PipelineDelegate next, Class<?> behaviorType, Object... constructorArguments) {
        if (PipelineBehavior.class.isAssignableFrom(behaviorType)) {
            return context -> {
                PipelineBehavior behavior = resolver.getServiceOrCreate(castType(behaviorType), constructorArguments);
                return behavior.handleAsync(context, next);
            };
        }

        Method method = resolveHandleMethod(behaviorType);
        Object[] ctorArgs = prepend(next, constructorArguments);
        Object instance = resolver.createInstance(castType(behaviorType), ctorArgs);

        return context -> invokeVoidLike(method, instance, context, resolver);
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> castType(Class<?> type) {
        return (Class<T>) type;
    }

    private static CompletionStage<Void> invokeVoidLike(Method method, Object instance, Object context, ServiceProvider resolver) {
        try {
            Object[] args = new Object[method.getParameterCount()];
            args[0] = context;
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 1; i < parameterTypes.length; i++) {
                args[i] = resolver.getRequiredService(parameterTypes[i]);
            }
            Object value = method.invoke(instance, args);
            if (value instanceof CompletionStage<?> stage) {
                return stage.thenApply(ignored -> null);
            }
            return CompletableFuture.completedFuture(null);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            return CompletableFuture.failedFuture(ex);
        }
    }

    private static Method resolveHandleMethod(Class<?> behaviorType) {
        Method[] candidates = Arrays.stream(behaviorType.getMethods())
                .filter(m -> HANDLE_METHOD_NAME.equals(m.getName()) || HANDLE_METHOD_NAME_ASYNC.equals(m.getName()))
                .filter(m -> m.getParameterCount() >= 1)
                .filter(m -> CompletionStage.class.isAssignableFrom(m.getReturnType()))
                .sorted(Comparator.comparingInt(Method::getParameterCount))
                .toArray(Method[]::new);

        if (candidates.length == 0) {
            throw new IllegalStateException("Method handle/handleAsync not found on " + behaviorType.getName());
        }
        if (candidates.length > 1) {
            throw new IllegalStateException("Multiple handle methods found on " + behaviorType.getName());
        }

        Method method = candidates[0];
        method.setAccessible(true);
        return method;
    }

    private static Object[] prepend(Object first, Object[] rest) {
        Object[] values = new Object[rest.length + 1];
        values[0] = first;
        System.arraycopy(rest, 0, values, 1, rest.length);
        return values;
    }
}
