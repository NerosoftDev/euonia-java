package com.euonia.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code SimpleServiceProvider} is a basic implementation of the {@link ServiceProvider}
 * interface
 * that uses a ConcurrentHashMap to store and retrieve service instances.
 * It allows for registering services and creating new instances using
 * reflection.
 * The createInstance method looks for a matching constructor based on the
 * provided
 * arguments and creates an instance of the specified type.
 */
public class SimpleServiceProvider implements ServiceProvider {
    private final Map<Class<?>, Object> services = new ConcurrentHashMap<>();

    public <T> void register(Class<T> type, T instance) {
        services.put(type, instance);
    }

    @Override
    public <T> Optional<T> getService(Class<T> type) {
        return Optional.ofNullable((T) services.get(type));
    }

    @Override
    public <T> Optional<T> getService(Class<T> type, Class<?>... genericTypeArguments) {
        return Optional.empty();
    }

    @Override
    public <T> Optional<T> getService(Class<T> type, String serviceName) {
        return Optional.ofNullable((T) services.get(type));
    }

    @Override
    public <T> T getRequiredService(Class<T> type) {
        return getService(type).orElseThrow(() -> new IllegalStateException("Cannot resolve service: " + type.getName()));
    }

    @Override
    public <T> List<T> getServices(Class<T> type) {
        return List.of();
    }

    @Override
    public <T> List<T> getServices(Class<T> type, Class<?>... genericTypeArguments) {
        return List.of();
    }

    @Override
    public <T> T createInstance(Class<T> type, Object... constructorArguments) {
        Constructor<?>[] constructors = type.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length != constructorArguments.length) {
                continue;
            }

            if (!isAssignable(parameterTypes, constructorArguments)) {
                continue;
            }

            try {
                constructor.setAccessible(true);
                @SuppressWarnings("unchecked")
                T created = (T) constructor.newInstance(constructorArguments);
                return created;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("Could not construct type: " + type.getName(), e);
            }
        }

        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                 | NoSuchMethodException e) {
            throw new IllegalStateException("Could not construct type: " + type.getName(), e);
        }
    }

    private static boolean isAssignable(Class<?>[] parameterTypes, Object[] args) {
        for (int i = 0; i < parameterTypes.length; i++) {
            Object arg = args[i];
            if (arg == null) {
                continue;
            }
            Class<?> parameterType = TypeHelper.boxIfPrimitive(parameterTypes[i]);
            if (!parameterType.isAssignableFrom(arg.getClass())) {
                return false;
            }
        }
        return true;
    }
}
