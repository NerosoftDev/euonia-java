package com.euonia.factory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import com.euonia.core.PriorityValueFinder;
import com.euonia.factory.annotation.FactoryCreate;
import com.euonia.factory.annotation.FactoryDelete;
import com.euonia.factory.annotation.FactoryExecute;
import com.euonia.factory.annotation.FactoryFetch;
import com.euonia.factory.annotation.FactoryInsert;
import com.euonia.factory.annotation.FactoryUpdate;
import com.euonia.osba.BusinessContext;
import com.euonia.osba.ExecutableObject;
import com.euonia.osba.ObjectEditState;
import com.euonia.osba.ObservableObject;
import com.euonia.osba.ReadOnlyObject;
import com.euonia.osba.abstracts.UseBusinessContext;
import com.euonia.reflection.ObjectReflector;
import com.euonia.reflection.ServiceProvider;

/**
 * BusinessObjectFactory is an implementation of the ObjectFactory interface that uses reflection to create, fetch, insert, update, save, execute, and delete business objects based on annotated factory methods.
 * It supports integration with a bean factory for object instantiation and handles different object states for saving operations.
 */
@SuppressWarnings("UnusedReturnValue")
public class BusinessObjectFactory implements ObjectFactory {

    private ServiceProvider provider;

    /**
     * Configures the BusinessObjectFactory to use the provided bean factory for object instantiation.
     *
     * @param resolver the ServiceProvider to be used for resolving services, including the bean factory
     * @return the current instance of BusinessObjectFactory
     */
    public BusinessObjectFactory use(ServiceProvider resolver) {
        this.provider = resolver;
        return this;
    }

    /**
     * Creates an instance of the specified type by finding and invoking the appropriate factory method annotated with @FactoryCreate, using the provided criteria as arguments.
     *
     * @param type     The class of the object to be created.
     * @param criteria The arguments to be used for creating the object.
     * @param <T>      The type of the object to be created.
     * @return The created object.
     */
    @Override
    public <T> T create(Class<T> type, Object... criteria) {
        var method = ObjectReflector.findFactoryMethod(type, FactoryCreate.class, criteria);
        var target = getObjectInstance(type);
        if (target instanceof ObservableObject<?> editableObject) {
            editableObject.markAsNew();
        }

        invoke(method, target, criteria);
        return target;
    }

    /**
     * Fetches an instance of the specified type by finding and invoking the appropriate factory method annotated with @FactoryFetch, using the provided criteria as arguments.
     *
     * @param type     The class of the object to be retrieved.
     * @param criteria The arguments to be used for retrieving the object.
     * @param <T>      The type of the object to be retrieved.
     * @return The retrieved object.
     */
    @Override
    public <T> T fetch(Class<T> type, Object... criteria) {
        var method = ObjectReflector.findFactoryMethod(type, FactoryFetch.class, criteria);
        var target = getObjectInstance(type);
        invoke(method, target, criteria);
        return target;
    }

    /**
     * Inserts a new instance of the specified type by finding and invoking the appropriate factory method annotated with @FactoryInsert, using the provided criteria as arguments.
     *
     * @param type     The class of the object to be inserted.
     * @param criteria The arguments to be used for inserting the object.
     * @param <T>      The type of the object to be inserted.
     * @return The inserted object.
     */
    @Override
    public <T> T insert(Class<T> type, Object... criteria) {
        var method = ObjectReflector.findFactoryMethod(type, FactoryInsert.class, criteria);
        var target = getObjectInstance(type);
        invoke(method, target, criteria);
        return target;
    }

    /**
     * Updates an existing instance of the specified type by finding and invoking the appropriate factory method annotated with @FactoryUpdate, using the provided criteria as arguments.
     *
     * @param type     The class of the object to be updated.
     * @param criteria The arguments to be used for updating the object.
     * @param <T>      The type of the object to be updated.
     * @return The updated object.
     */
    @Override
    public <T> T update(Class<T> type, Object... criteria) {
        var method = ObjectReflector.findFactoryMethod(type, FactoryUpdate.class, criteria);
        var target = getObjectInstance(type);
        invoke(method, target, criteria);
        return target;
    }

    /**
     * Saves the provided object by determining its state and invoking the appropriate factory method based on annotations.
     *
     * @param type   The class of the object to be saved.
     * @param target The instance of the object to be saved.
     * @param <T>    The type of the object to be saved.
     * @return The saved object.
     */
    @Override
    public <T> T save(Class<T> type, T target) {
        Method method;
        if (target instanceof ObservableObject<?> editableObject) {
            if (editableObject.getState() == ObjectEditState.NEW) {
                method = ObjectReflector.findFactoryMethod(type, FactoryInsert.class, new Object[0]);
            } else if (editableObject.getState() == ObjectEditState.CHANGED) {
                method = ObjectReflector.findFactoryMethod(type, FactoryUpdate.class, new Object[0]);
            } else if (editableObject.getState() == ObjectEditState.DELETED) {
                method = ObjectReflector.findFactoryMethod(type, FactoryDelete.class, new Object[0]);
            } else {
                throw new IllegalArgumentException("Unexpected value: " + editableObject.getState());
            }
        } else if (target instanceof ExecutableObject) {
            method = ObjectReflector.findFactoryMethod(type, FactoryExecute.class, new Object[0]);
        } else if (target instanceof ReadOnlyObject) {
            throw new UnsupportedOperationException("Cannot save a read-only object of type: " + type.getName());
        } else {
            method = ObjectReflector.findFactoryMethod(type, FactoryUpdate.class, new Object[0]);
        }
        invoke(method, target);
        return target;
    }

    /**
     * Executes the provided object by finding and invoking the appropriate factory method annotated with @FactoryExecute, using the provided criteria as arguments.
     *
     * @param type     The class of the object to be executed.
     * @param criteria The arguments to be used for executing the object.
     * @param <T>      The type of the object to be executed.
     * @return The executed object.
     */
    @Override
    public <T> T execute(Class<T> type, Object... criteria) {
        var method = ObjectReflector.findFactoryMethod(type, FactoryExecute.class, criteria);
        var target = getObjectInstance(type);
        invoke(method, target, criteria);
        return target;
    }

    /**
     * Deletes the specified object by finding and invoking the appropriate factory method annotated with @FactoryDelete, using the provided criteria as arguments.
     *
     * @param type     The class of the object to be deleted.
     * @param criteria The arguments to be used for deleting the object.
     * @param <T>      The type of the object to be deleted.
     */
    @Override
    public <T> void delete(Class<T> type, Object... criteria) {
        var method = ObjectReflector.findFactoryMethod(type, FactoryDelete.class, criteria);
        var target = getObjectInstance(type);
        invoke(method, target, criteria);
    }

    /**
     * Creates an instance of the specified type using the bean factory if available, or falls back to reflection-based instantiation.
     * If the created object implements UseBusinessContext, it sets the business context for that object.
     *
     * @param type The class of the object to be created.
     * @param <T>  The type of the object to be created.
     * @return The created object instance.
     */
    @SuppressWarnings("unchecked")
    private <T> T getObjectInstance(Class<T> type) {
        T object = PriorityValueFinder.find(queue -> {
            if (provider != null) {
                queue.add(() -> {
                    try {
                        return provider.getService(type).orElse(null);
                    } catch (Exception e) {
                        return null;
                    }
                }, 1);
            }
            queue.add(() -> {
                try {
                    var constructors = Arrays.stream(type.getDeclaredConstructors())
                                             .sorted((a, b) -> Integer.compare(b.getParameterCount(), a.getParameterCount()))
                                             .toList();

                    var ctor = constructors.stream().findFirst().orElse(null);

                    if(ctor == null) {
                        throw new RuntimeException("No constructor found for type: " + type.getName());
                    }

                    var parameters = ctor.getParameters();
                    if (parameters.length == 0) {
                        return (T) ctor.newInstance();
                    } else {
                        var args = new Object[parameters.length];
                        for (int i = 0; i < parameters.length; i++) {
                            var parameterType = parameters[i].getType();
                            args[i] = getObjectInstance(parameterType);
                        }
                        return (T) ctor.newInstance(args);
                    }
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
                         InvocationTargetException e) {
                    throw new RuntimeException("Failed to create instance of " + type.getName(), e);
                }
            }, 2);
        }, Objects::nonNull, null);

        if (object instanceof UseBusinessContext businessObject) {
            businessObject.setBusinessContext(new BusinessContext(this::getObjectInstance, this));
        }

        return object;
    }

    /**
     * Invokes the specified method on the target object with the given criteria as arguments, handling any exceptions that may occur during invocation.
     *
     * @param method   The method to be invoked.
     * @param target   The target object on which the method is to be invoked.
     * @param criteria The arguments to be used for invoking the method.
     * @param <T>      The type of the target object.
     */
    private <T> void invoke(Method method, T target, Object... criteria) {
        try {
            method.setAccessible(true);
            method.invoke(target, criteria);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(
                "Failed to invoke method: " + method.getName() + " on target: " + target.getClass().getName(), e);
        }
    }

}
