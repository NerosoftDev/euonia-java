package com.euonia.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

/**
 * DelegateServiceResolver is an implementation of the ServiceResolver interface
 * that delegates service resolution to a provided Function.
 * It allows for flexible service resolution by using a custom Function to
 * retrieve services based on their class type.
 * The createInstance method uses reflection to find a matching constructor and
 * create an instance of the specified type with the provided constructor
 * arguments.
 */
public class DelegateServiceResolver implements ServiceResolver {

    private final Function<Class<?>, ?> beanFactory;

    /**
     * Creates a new instance of DelegateServiceResolver with the given bean factory
     * function.
     *
     * @param beanFactory the function used to resolve services based on their class
     *                    type
     */
    public DelegateServiceResolver(Function<Class<?>, ?> beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public <T> T getService(Class<T> type) {
        return (T) beanFactory.apply(type);
    }

    @Override
    public <T> T getRequiredService(Class<T> type) {
        T instance = (T) beanFactory.apply(type);
        if (instance == null) {
            throw new IllegalStateException("Required service not found: " + type.getName());
        }
        return instance;
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
