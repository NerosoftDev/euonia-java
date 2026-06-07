package com.euonia.osba;

/**
 * Represents the edit state of an object, indicating whether it is new, changed, deleted, or unchanged.
 * This enum is used to track the state of an object during its lifecycle, allowing for proper handling of changes and deletions in the context of business rules and data persistence.
 */
public enum ObjectEditState {

    /**
     * Indicates that the object is unchanged, meaning it has not been modified since it was last saved to the database.
     */
    NONE,
    /**
     * Indicates that the object is new, meaning it has been created but not yet saved to the database. This state is used to track newly created objects that need to be saved.
     */
    NEW,
    /**
     * Indicates that the object has been changed, meaning it has been modified since it was last saved to the database. This state is used to track modified objects that need to be saved.
     */
    CHANGED,
    /**
     * Indicates that the object has been marked as deleted, meaning it has been flagged for deletion but not yet removed from the database. This state is used to track objects that need to be deleted.
     */
    DELETED
}
