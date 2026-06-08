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
        return guid(GuidType.SIMPLE);
    }

    /**
     * Generates a new ObjectId using the GUID algorithm.
     *
     * @param type the GUID generation strategy
     * @return a new ObjectId
     */
    public static ObjectId guid(GuidType type) {
        return new ObjectId(GuidGenerator.generate(type));
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

        if (value instanceof UUID target) {
            return target.toString();
        }

        if (value instanceof Integer target) {
            return target.toString();
        }

        if (value instanceof Long target) {
            return target.toString();
        }

        if (value instanceof String target) {
            return target;
        }

        if (value instanceof ULID target) {
            return target.toString();
        }

        return value.toString();
    }
}
