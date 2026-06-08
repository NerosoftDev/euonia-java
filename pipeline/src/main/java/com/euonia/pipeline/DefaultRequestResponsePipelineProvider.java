package com.euonia.pipeline;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.euonia.reflection.ServiceResolver;

public class DefaultRequestResponsePipelineProvider<TRequest, TResponse> extends RequestResponsePipelineBase<TRequest, TResponse> {
    private static final String HANDLE_METHOD_NAME = "handle";
    private static final String HANDLE_METHOD_NAME_ASYNC = "handleAsync";

    private final ServiceResolver resolver;

    public DefaultRequestResponsePipelineProvider(ServiceResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    protected RequestResponsePipelineDelegate<TRequest, TResponse> getNext(RequestResponsePipelineDelegate<TRequest, TResponse> next,
                                                                           Class<?> type,
                                                                           Object... constructorArguments) {
        if (RequestResponsePipelineBehavior.class.isAssignableFrom(type)) {
            return request -> {
                RequestResponsePipelineBehavior<TRequest, TResponse> behavior = resolver.getServiceOrCreate(castType(type), constructorArguments);
                return behavior.handleAsync(request, next);
            };
        }

        Method method = resolveHandleMethod(type);
        Object[] ctorArgs = prepend(next, constructorArguments);
        Object instance = resolver.createInstance(castType(type), ctorArgs);

        return request -> invokeTyped(method, instance, request, resolver);
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> castType(Class<?> type) {
        return (Class<T>) type;
    }

    @SuppressWarnings("unchecked")
    private CompletionStage<TResponse> invokeTyped(Method method, Object instance, TRequest request, ServiceResolver resolver) {
        try {
            Object[] args = new Object[method.getParameterCount()];
            args[0] = request;
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 1; i < parameterTypes.length; i++) {
                args[i] = resolver.getRequiredService(parameterTypes[i]);
            }
            Object value = method.invoke(instance, args);
            if (!(value instanceof CompletionStage<?> stage)) {
                return CompletableFuture.failedFuture(new IllegalStateException("Handle method must return CompletionStage."));
            }
            return (CompletionStage<TResponse>) stage;
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
