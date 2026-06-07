package com.euonia.tuple;

import java.io.Serial;
import java.util.List;

/**
 * Represents a tuple of a single value, which can be of any type.
 * This class is immutable and provides methods to access the value and perform common tuple operations.
 *
 * @param value the value of the tuple
 * @param <V>   the type of the value
 */
public record Solo<V>(V value) implements Tuple {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final int SIZE = 1;

    /**
     * Creates a new Solo instance with the specified value.
     *
     * @param value the value of the tuple
     * @param <V>   the type of the value
     * @return a new Solo instance with the specified value
     */
    public static <V> Solo<V> of(V value) {
        return new Solo<>(value);
    }

    /**
     * Creates a new Solo instance from the specified value.
     *
     * @param values the array of values
     * @param <X>    the type of the value
     * @return a new Solo instance with the specified value
     */
    public static <X> Solo<X> from(final X[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("values must not be empty");
        }
        if (values.length != SIZE) {
            throw new IllegalArgumentException("values must have same size");
        }
        return new Solo<>(values[0]);
    }

    /**
     * Creates a new Solo instance from the specified value.
     *
     * @param values the list of values
     * @param <X>    the type of the value
     * @return a new Solo instance with the specified value
     */
    public static <X> Solo<X> from(final List<X> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("values must not be empty");
        }
        if (values.size() != SIZE) {
            throw new IllegalArgumentException("values must have same size");
        }
        return new Solo<>(values.get(0));
    }

    @Override
    public int size() {
        return SIZE;
    }

    @Override
    public java.util.List<Object> values() {
        return java.util.Collections.singletonList(value);
    }

    @Override
    public boolean contains(Object value) {
        return java.util.Objects.equals(this.value, value);
    }
}
