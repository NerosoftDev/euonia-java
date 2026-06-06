package com.euonia.osba;

import com.euonia.factory.ObjectFactory;

import java.util.Objects;
import java.util.function.Function;

/**
 * BusinessContext provides a small service-resolution and instance-creation
 * gateway for business objects created by OSBA factories.
 */
public final class BusinessContext {
    private final Function<Class<?>, ?> instanceCreator;
    private final ObjectFactory objectFactory;

    /**
     * Creates a new instance of BusinessContext with the specified instance creator function and ObjectFactory.
     *
     * @param instanceCreator A function that creates instances of the specified type.
     * @param objectFactory   The ObjectFactory to be used by this BusinessContext.
     */
    public BusinessContext(Function<Class<?>, ?> instanceCreator, ObjectFactory objectFactory) {
        this.instanceCreator = instanceCreator;
        this.objectFactory = objectFactory;
    }

    /**
     * Gets an instance of the specified type from the context, creating it if necessary.
     *
     * @param objectType The type of the object to retrieve or create.
     * @param <T>        The type of the object.
     * @return An instance of the specified type.
     */
    @SuppressWarnings("unchecked")
    public <T> T getOrCreateObject(Class<T> objectType) {
        Objects.requireNonNull(objectType, "objectType");
        if (instanceCreator == null) {
            throw new IllegalStateException("BusinessContext does not support instance creation.");
        }
        return (T) instanceCreator.apply(objectType);
    }

    /**
     * Gets the ObjectFactory associated with this BusinessContext, which can be used to create, fetch, insert, update, save, execute, and delete business objects based on annotated factory methods.
     *
     * @return The ObjectFactory associated with this BusinessContext.
     */
    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }
}
