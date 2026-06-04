package com.euonia.osba.abstracts;

/**
 * Marks an object that can be saved to a database or other persistent storage.
 *
 * @param <T> The type of the object being saved.
 */
public interface Savable<T> {
    void saveComplete(T newObject);

    T save(boolean forceUpdate);
}
