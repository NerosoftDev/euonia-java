package com.euonia.reflection;

/**
 * ServiceResolver is an interface that defines methods for resolving and
 * creating service instances.
 * Implementations of this interface can provide custom logic for retrieving and
 * instantiating services.
 */
public interface ServiceResolver {
    /**
     * Retrieves a service instance of the specified type. If the service is not
     * found, it returns null.
     *
     * @param <T>  the type of the service
     * @param type the class of the service
     * @return the service instance or null if not found
     */
    <T> T getService(Class<T> type);

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
        T service = getService(type);
        return service != null ? service : createInstance(type, constructorArguments);
    }
}
