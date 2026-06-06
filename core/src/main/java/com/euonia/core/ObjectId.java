package com.euonia.core;

import java.util.UUID;

/**
 * ObjectId is a class that represents a unique identifier for an object.
 * It can be generated using different algorithms such as Snowflake, GUID,
 * Random, and ULID.
 * The value of the ObjectId can be of type long, String, UUID, or Integer.
 * The class provides methods to generate ObjectIds using different algorithms
 * and overrides the hashCode, equals, and toString methods for proper
 * functionality.
 * The ObjectId class is immutable, meaning that once an ObjectId is created,
 * its value cannot be changed.
 * This ensures that the uniqueness of the identifier is maintained throughout
 * its lifecycle.
 */
@SuppressWarnings("unused")
public final class ObjectId {
    private final Object value;

    /**
     * Gets the value of the ObjectId.
     *
     * @return the value of the ObjectId
     */
    public Object getValue() {
        return value;
    }

    /**
     * Constructs an ObjectId with the specified value.
     *
     * @param value the value of the ObjectId
     */
    public ObjectId(long value) {
        this.value = value;
    }

    /**
     * Constructs an ObjectId with the specified value.
     *
     * @param value the value of the ObjectId
     */
    public ObjectId(String value) {
        this.value = value;
    }

    /**
     * Constructs an ObjectId with the specified value.
     *
     * @param value the value of the ObjectId
     */
    public ObjectId(UUID value) {
        this.value = value;
    }

    /**
     * Constructs an ObjectId with the specified value.
     *
     * @param value the value of the ObjectId
     */
    public ObjectId(Integer value) {
        this.value = value;
    }

    /**
     * Generates a new ObjectId using the Snowflake algorithm.
     *
     * @return a new ObjectId
     */
    public static ObjectId snowflake() {
        return new ObjectId(SnowflakeId.getInstance().nextId());
    }

    /**
     * Generates a new ObjectId using the GUID algorithm.
     *
     * @return a new ObjectId
     */
    public static ObjectId guid() {
        return new ObjectId(UUID.randomUUID());
    }

    /**
     * Generates a new ObjectId using the Random algorithm.
     *
     * @return a new ObjectId
     */
    public static ObjectId random() {
        return new ObjectId(UUID.randomUUID());
    }

    /**
     * Generates a new ObjectId using the ULID algorithm.
     *
     * @return a new ObjectId
     */
    public static ObjectId ulid() {
        return new ObjectId(ULID.generate());
    }

    public <T> T getValue(Class<T> type) {
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ObjectId objectId = (ObjectId) obj;
        return value.equals(objectId.value);
    }

    @Override
    public String toString() {
        return switch (value) {
            case UUID uuid -> uuid.toString();
            case ULID uuid -> ulid().toString();
            case Long l -> l.toString();
            case Integer i -> i.toString();
            case String s -> s;
            default -> value.toString();
        };
    }
}
