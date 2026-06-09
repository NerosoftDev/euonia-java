package com.euonia.domain;

/**
 * The Entity interface represents a generic entity in the domain model. It defines the basic properties and methods that all entities should have.
 * Entities are objects that have a distinct identity and can be distinguished from other objects based on their identity rather than their attributes.
 * They typically represent real-world concepts or objects in the system and are often used to model business entities or domain objects.
 */
public interface Entity<ID extends Comparable<ID>> {

    /**
     * Gets the identifier for the entity. The identifier is a unique value that distinguishes this entity from others.
     *
     * @return the unique identifier of the entity
     */
    ID getId();

    /**
     * Sets the identifier for the entity. The identifier is a unique value that distinguishes this entity from others.
     *
     * @param id the unique identifier to set for this entity
     */
    void setId(ID id);

    /**
     * Gets the keys that uniquely identify this entity. By default, it returns an array containing the entity's identifier.
     *
     * @return an array of objects representing the keys that uniquely identify this entity
     */
    default Object[] getKeys() {
        return new Object[]{getId()};
    }
}
