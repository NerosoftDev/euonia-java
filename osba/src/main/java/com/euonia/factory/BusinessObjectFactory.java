package com.euonia.factory;

import com.euonia.factory.annotation.*;
import com.euonia.osba.EditableObject;
import com.euonia.osba.ExecutableObject;
import com.euonia.osba.ObjectEditState;
import com.euonia.osba.ReadOnlyObject;
import com.euonia.reflection.ObjectReflector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;

/**
 * BusinessObjectFactory is an implementation of the ObjectFactory interface that uses reflection to create, fetch, insert, update, save, execute, and delete business objects based on annotated factory methods.
 * It supports integration with a bean factory for object instantiation and handles different object states for saving operations.
 */
public class BusinessObjectFactory implements ObjectFactory {

    private Function<Class<?>, ?> beanFactory;

    public BusinessObjectFactory use(Function<Class<?>, ?> beanFactory) {
        this.beanFactory = beanFactory;
        return this;
    }

    @Override
    public <T> T create(Class<T> type, Object... criteria) {
        var method = ObjectReflector.findFactoryMethod(type, FactoryCreate.class, criteria);
        var target = getObjectInstance(type);
        if (target instanceof EditableObject editableObject) {

        }

        invoke(method, target, criteria);
        return target;
    }

    @Override
    public <T> T fetch(Class<T> type, Object... criteria) {
        var method = ObjectReflector.findFactoryMethod(type, FactoryFetch.class, criteria);
        var target = getObjectInstance(type);
        invoke(method, target, criteria);
        return target;
    }

    @Override
    public <T> T insert(Class<T> type, Object... criteria) {
        var method = ObjectReflector.findFactoryMethod(type, FactoryInsert.class, criteria);
        var target = getObjectInstance(type);
        invoke(method, target, criteria);
        return target;
    }

    @Override
    public <T> T update(Class<T> type, Object... criteria) {
        var method = ObjectReflector.findFactoryMethod(type, FactoryUpdate.class, criteria);
        var target = getObjectInstance(type);
        invoke(method, target, criteria);
        return target;
    }

    @Override
    public <T> T save(Class<T> type, T target) {
        Method method = switch (target) {
            case EditableObject editableObject -> switch (editableObject.getEditState()) {
                case ObjectEditState.NEW ->
                    ObjectReflector.findFactoryMethod(type, FactoryInsert.class, new Object[0]);
                case ObjectEditState.CHANGED ->
                    ObjectReflector.findFactoryMethod(type, FactoryUpdate.class, new Object[0]);
                case ObjectEditState.DELETED ->
                    ObjectReflector.findFactoryMethod(type, FactoryDelete.class, new Object[0]);
                default -> throw new IllegalArgumentException("Unexpected value: " + editableObject.getEditState());
            };
            case ExecutableObject _ -> ObjectReflector.findFactoryMethod(type, FactoryExecute.class, new Object[0]);
            case ReadOnlyObject _ ->
                throw new UnsupportedOperationException("Cannot save a read-only object of type: " + type.getName());
            default -> ObjectReflector.findFactoryMethod(type, FactoryUpdate.class, new Object[0]);
        };
        invoke(method, target, new Object[0]);
        return target;
    }

    @Override
    public <T> T execute(Class<T> type, Object... criteria) {
        var method = ObjectReflector.findFactoryMethod(type, FactoryExecute.class, criteria);
        var target = getObjectInstance(type);
        invoke(method, target, criteria);
        return target;
    }

    @Override
    public <T> void delete(Class<T> type, Object... criteria) {
        var method = ObjectReflector.findFactoryMethod(type, FactoryDelete.class, criteria);
        var target = getObjectInstance(type);
        invoke(method, target, criteria);
    }

    @SuppressWarnings("unchecked")
    private <T> T getObjectInstance(Class<T> type) {
        try {
            T object = null;
            if (beanFactory != null) {
                try {
                    object = (T) beanFactory.apply(type);
                } catch (Exception e) {
                    // Ignore exceptions from bean factory and fallback to reflection
                }
            }
            if (object == null) {
                var constructors = Arrays.stream(type.getDeclaredConstructors())
                        .sorted((a, b) -> Integer.compare(b.getParameterCount(), a.getParameterCount()))
                        .toList();

                var ctor = constructors.stream().findFirst().orElseThrow();

                var parameters = ctor.getParameters();
                if (parameters.length == 0) {
                    return (T) ctor.newInstance();
                } else {
                    var args = new Object[parameters.length];
                    for (int i = 0; i < parameters.length; i++) {
                        var parameterType = parameters[i].getType();
                        args[i] = getObjectInstance(parameterType);
                    }
                    object = (T) ctor.newInstance(args);
                }
            }
            return object;

        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException
                | InvocationTargetException e) {
            throw new RuntimeException("Failed to create instance of " + type.getName(), e);
        }

    }

    private <T> void invoke(Method method, T target, Object... criteria) {
        try {
            method.invoke(target, criteria);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(
                    "Failed to invoke method: " + method.getName() + " on target: " + target.getClass().getName(), e);
        }
    }

}
