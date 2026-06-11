package com.euonia.reflection;

import java.lang.reflect.Constructor;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

public class ApplicationContextServiceProvider implements ServiceProvider {
    private final ApplicationContext applicationContext;

    public ApplicationContextServiceProvider(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public <T> T getService(Class<T> type) {
        return applicationContext.getBeanProvider(type).getIfAvailable();
    }

    @Override
    public <T> T getRequiredService(Class<T> type) {
        return applicationContext.getBean(type);
    }

    @Override
    public <T> T createInstance(Class<T> type, Object... constructorArguments) {
        AutowireCapableBeanFactory factory = applicationContext.getAutowireCapableBeanFactory();
        if (constructorArguments == null || constructorArguments.length == 0) {
            return type.cast(factory.createBean(type));
            //return type.cast(factory.createBean(type, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false));
        }

        Constructor<?>[] constructors = type.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length != constructorArguments.length) {
                continue;
            }

            if (!isAssignable(parameterTypes, constructorArguments)) {
                continue;
            }

            constructor.setAccessible(true);
            @SuppressWarnings("unchecked")
            T instance = (T) BeanUtils.instantiateClass(constructor, constructorArguments);
            factory.autowireBean(instance);
            return type.cast(factory.initializeBean(instance, type.getName()));
        }

        throw new IllegalStateException("Could not find matching constructor for " + type.getName());
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
