package com.euonia.domain;

/**
 * The Entity interface represents a generic entity in the domain model. It defines the basic properties and methods that all entities should have.
 * Entities are objects that have a distinct identity and can be distinguished from other objects based on their identity rather than their attributes.
 * They typically represent real-world concepts or objects in the system and are often used to model business entities or domain objects.
 */
public interface Entity<ID extends Comparable<ID>> {
    ID getId();

    void setId(ID id);

    Object[] getKeys();
}
