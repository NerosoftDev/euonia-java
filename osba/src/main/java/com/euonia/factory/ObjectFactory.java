package com.euonia.factory;

/**
 * ObjectFactory is an interface that defines methods for business object
 * creation, retrieval, update, and deletion.
 * It provides a standardized way to manage the lifecycle of business objects in
 * an application. The methods include:
 * <p>
 * - create: Creates a new instance of the specified type with the given
 * arguments.
 * </P>
 * <p>
 * - fetch: Retrieves an existing instance of the specified type based on the
 * given arguments.
 * </P>
 * <p>
 * - insert: Inserts a new instance of the specified type into the data store
 * with the given arguments.
 * </P>
 * <p>
 * - update: Updates an existing instance of the specified type in the data
 * store with the given arguments.
 * </P>
 * <p>
 * - save: Saves the provided instance of the specified type to the data store,
 * either by inserting or updating it as necessary.
 * </P>
 * <p>
 * - execute: Executes a specific operation related to the specified type with
 * the given arguments, which may involve complex business logic.
 * </P>
 * <p>
 * - delete: Deletes an existing instance of the specified type from the data
 * store based on the given arguments.
 * </p>
 */
public interface ObjectFactory {
    /**
     * Creates a new instance of the specified type with the given arguments.
     * The implementation of this method should handle the instantiation logic based
     * on the type and arguments provided.
     * This may involve using reflection, dependency injection, or other mechanisms
     * to create the object.
     * The method should return the newly created instance of the specified type.
     *
     * @param <T>  The type of the object to be created.
     * @param type The class of the object to be created.
     * @param args The arguments to be used for creating the object.
     * @return The newly created instance of the specified type.
     */
    <T> T create(Class<T> type, Object... args);

    /**
     * Retrieves an existing instance of the specified type based on the given
     * arguments.
     * The implementation of this method should handle the retrieval logic based on
     * the type and arguments provided.
     * This may involve querying a database, accessing a cache, or other mechanisms
     * to find the object.
     * The method should return the retrieved instance of the specified type, or
     * null if no matching instance is found.
     *
     * @param <T>  The type of the object to be retrieved.
     * @param type The class of the object to be retrieved.
     * @param args The arguments to be used for retrieving the object.
     * @return The retrieved instance of the specified type, or null if no matching
     *         instance is found.
     */
    <T> T fetch(Class<T> type, Object... args);

    /**
     * Inserts a new instance of the specified type into the data store with the
     * given arguments.
     * The implementation of this method should handle the insertion logic based on
     * the type and arguments provided.
     * This may involve validating the input, preparing the data for insertion, and
     * executing the necessary database operations to persist the object.
     * The method should return the newly inserted instance of the specified type,
     * which may include any generated identifiers or additional data from the
     * data store.
     *
     * @param <T>  The type of the object to be inserted.
     * @param type The class of the object to be inserted.
     * @param args The arguments to be used for inserting the object.
     * @return The newly inserted instance of the specified type, which may include
     *         any generated identifiers or additional data from the data store.
     */
    <T> T insert(Class<T> type, Object... args);

    /**
     * Updates an existing instance of the specified type in the data store with the
     * given arguments.
     * The implementation of this method should handle the update logic based on the
     * type and arguments provided.
     * This may involve validating the input, preparing the data for update, and
     * executing the necessary database operations to modify the existing object.
     * The method should return the updated instance of the specified type, which
     * may include any changes made during the update process.
     *
     * @param <T>  The type of the object to be updated.
     * @param type The class of the object to be updated.
     * @param args The arguments to be used for updating the object.
     * @return The updated instance of the specified type, which may include any
     *         changes made during the update process.
     */
    <T> T update(Class<T> type, Object... args);

    /**
     * Saves the provided instance of the specified type to the data store, either
     * by inserting or updating it as necessary.
     * The implementation of this method should determine whether to insert a new
     * instance or update an existing one based on the state of the provided object
     * and any relevant identifiers.
     * This may involve validating the input, preparing the data for persistence, and
     * executing the necessary database operations to save the object.
     * The method should return the saved instance of the specified type, which may
     * include any changes made during the save process, such as generated
     * identifiers or updated fields.
     *
     * @param <T>    The type of the object to be saved.
     * @param type   The class of the object to be saved.
     * @param target The instance of the object to be saved.
     * @return The saved instance of the specified type, which may include any
     *         changes made during the save process, such as generated identifiers
     *         or updated fields.
     */
    <T> T save(Class<T> type, T target);

    /**
     * Executes a custom operation on the specified type with the given arguments.
     * The implementation of this method should handle the execution logic based on
     * the type and arguments provided.
     * This may involve validating the input, preparing the data for execution, and
     * executing the necessary operations.
     * The method should return the result of the execution, which may include any
     * changes made during the process.
     *
     * @param <T>  The type of the object to be executed.
     * @param type The class of the object to be executed.
     * @param args The arguments to be used for executing the object.
     * @return The result of the execution, which may include any changes made during
     *         the process.
     */
    <T> T execute(Class<T> type, Object... args);

    /**
     * Deletes an existing instance of the specified type from the data store with the
     * given arguments.
     * The implementation of this method should handle the deletion logic based on the
     * type and arguments provided.
     * This may involve validating the input, preparing the data for deletion, and
     * executing the necessary database operations to remove the object.
     *
     * @param <T>  The type of the object to be deleted.
     * @param type The class of the object to be deleted.
     * @param args The arguments to be used for deleting the object.
     */
    <T> void delete(Class<T> type, Object... args);
}
