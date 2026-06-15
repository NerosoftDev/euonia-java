package com.euonia.reflection;

import java.util.List;
import java.util.Optional;

/**
 * ServiceProvider is an interface that defines methods for resolving and
 * creating service instances.
 * Implementations of this interface can provide custom logic for retrieving and
 * instantiating services.
 */
public interface ServiceProvider {
    /**
     * Retrieves a service instance of the specified type.
     *
     * @param <T>  the type of the service
     * @param type the class of the service
     * @return an Optional containing the service instance, or empty if not found
     */
    <T> Optional<T> getService(Class<T> type);

    /**
     * Retrieves a service instance of the specified type and generic type arguments.
     *
     * @param <T>                  the type of the service
     * @param type                 the class of the service
     * @param genericTypeArguments the generic type arguments of the service
     * @return an Optional containing the service instance, or empty if not found
     */
    <T> Optional<T> getService(Class<T> type, Class<?>... genericTypeArguments);

    /**
     * Retrieves a service instance of the specified type and service name.
     *
     * @param <T>         the type of the service
     * @param type        the class of the service
     * @param serviceName the name of the service
     * @return an Optional containing the service instance, or empty if not found
     */
    <T> Optional<T> getService(Class<T> type, String serviceName);

    /**
     * Retrieves a required service instance of the specified type. If the service
     * is not found, it throws an exception.
     *
     * @param <T>  the type of the service
     * @param type the class of the service
     * @return the service instance
     * @throws IllegalStateException if the service is not found
     */
    <T> T getRequiredService(Class<T> type);

    /**
     * Retrieves all service instances of the specified type.
     *
     * @param <T>  the type of the service
     * @param type the class of the service
     * @return a list of service instances
     */
    <T> List<T> getServices(Class<T> type);

    /**
     * Retrieves all service instances of the specified type and generic type arguments.
     *
     * @param <T>                  the type of the service
     * @param type                 the class of the service
     * @param genericTypeArguments the generic type arguments of the service
     * @return a list of service instances
     */
    <T> List<T> getServices(Class<T> type, Class<?>... genericTypeArguments);

    /**
     * Creates a new instance of the specified type using the provided constructor
     * arguments.
     *
     * @param <T>                  the type of the service
     * @param type                 the class of the service
     * @param constructorArguments the arguments to pass to the constructor
     * @return the created instance
     * @throws IllegalStateException if the instance cannot be created
     */
    <T> T createInstance(Class<T> type, Object... constructorArguments);

    /**
     * Retrieves a service instance of the specified type, or creates a new instance
     * if the service is not found.
     *
     * @param <T>                  the type of the service
     * @param type                 the class of the service
     * @param constructorArguments the arguments to pass to the constructor if a new
     *                             instance is created
     * @return the service instance or a newly created instance
     */
    default <T> T getServiceOrCreate(Class<T> type, Object... constructorArguments) {
        return getService(type).orElseGet(() -> createInstance(type, constructorArguments));
    }
}
