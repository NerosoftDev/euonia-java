package com.euonia.osba.abstracts;

/**
 * TrackableObject is an interface that defines methods for tracking the state
 * of an object in terms of its validity, changes, deletion status, and whether
 * it is new or can be saved. This interface is typically used in business
 * objects to manage their lifecycle and ensure that they are in a consistent
 * state before performing operations such as saving or deleting.
 * <p>
 * The methods defined in this interface include:
 * <p>
 * - isValid(): Indicates whether the object is in a valid state. This is used
 * to determine whether the object can be saved or not.
 * <p>
 * - isChanged(): Indicates whether the object has been changed since it was
 * last saved.
 * <p>
 * - isDeleted(): Indicates whether the object has been marked for deletion.
 * <p>
 * - isNew(): Indicates whether the object is new and has not been saved yet.
 * <p>
 * - isSavable(): Indicates whether the object can be saved.
 * <p>
 * Implementing this interface allows business objects to manage their state
 * effectively and ensure that operations are performed only when the object is
 * in an appropriate state. For example, an object that is not valid should
 * not be saved, and an object that is marked for deletion should not be updated.
 */
public interface TrackableObject {

    /**
     * Indicates whether the object is in a valid state. This is used to determine
     * whether the object can be saved or not.
     *
     * @return true if the object is valid, false otherwise
     */
    boolean isValid();

    /**
     * Indicates whether the object has been changed since it was last saved.
     *
     * @return true if the object has been changed, false otherwise
     */
    boolean isChanged();

    /**
     * Indicates whether the object has been marked for deletion.
     *
     * @return true if the object is marked for deletion, false otherwise
     */
    boolean isDeleted();

    /**
     * Indicates whether the object is new and has not been saved yet.
     *
     * @return true if the object is new, false otherwise
     */
    boolean isNew();

    /**
     * Indicates whether the object can be saved.
     *
     * @return true if the object can be saved, false otherwise
     */
    boolean isSavable();
}
