package com.euonia.pipeline;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.euonia.reflection.ServiceProvider;

/**
 * DefaultPipelineProvider is a concrete implementation of Pipeline that uses reflection to invoke pipeline behaviors. It supports both synchronous and asynchronous handle methods, as well as behaviors that implement the PipelineBehavior interface.
 */
public class DefaultPipelineProvider<C, R> extends PipelineBase<C, R> {
    private static final String HANDLE_METHOD_NAME = "handle";
    private static final String HANDLE_METHOD_NAME_ASYNC = "handleAsync";

    private final ServiceProvider provider;

    public DefaultPipelineProvider(ServiceProvider provider) {
        this.provider = provider;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected PipelineDelegate<C, R> getNext(PipelineDelegate<C, R> next, Class<?> behaviorType, Object... constructorArguments) {
        if (PipelineBehavior.class.isAssignableFrom(behaviorType)) {
            return request -> {
                PipelineBehavior<C, R> behavior = provider.getServiceOrCreate(castType(behaviorType), constructorArguments);
                return behavior.handleAsync(request, next);
            };
        }

        Method method = resolveHandleMethod(behaviorType);
        Object[] ctorArgs = prepend(next, constructorArguments);
        Object instance = provider.createInstance(castType(behaviorType), ctorArgs);

        return request -> invokeTyped(method, instance, request);
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> castType(Class<?> type) {
        return (Class<T>) type;
    }

    @SuppressWarnings("unchecked")
    private CompletionStage<R> invokeTyped(Method method, Object instance, C request) {
        try {
            Object[] args = new Object[method.getParameterCount()];
            args[0] = request;
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 1; i < parameterTypes.length; i++) {
                args[i] = this.provider.getRequiredService(parameterTypes[i]);
            }
            Object value = method.invoke(instance, args);
            if (value instanceof CompletionStage<?> stage) {
                return (CompletionStage<R>) stage.thenApply(v -> (R) v);
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
