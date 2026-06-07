package com.euonia.tuple;

import java.io.Serial;
import java.util.List;

/**
 * A tuple of three values.
 *
 * @param value1 the first value of the tuple
 * @param value2 the second value of the tuple
 * @param value3 the third value of the tuple
 * @param <T1>   the type of the first value
 * @param <T2>   the type of the second value
 * @param <T3>   the type of the third value
 */
public record Trio<T1, T2, T3>(T1 value1, T2 value2, T3 value3) implements Tuple {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final int SIZE = 3;

    /**
     * Creates a new Trio instance with the specified values.
     *
     * @param value1 the first value of the tuple
     * @param value2 the second value of the tuple
     * @param value3 the third value of the tuple
     * @param <T1>   the type of the first value
     * @param <T2>   the type of the second value
     * @param <T3>   the type of the third value
     * @return a new Trio instance with the specified values
     */
    public static <T1, T2, T3> Trio<T1, T2, T3> of(T1 value1, T2 value2, T3 value3) {
        return new Trio<>(value1, value2, value3);
    }

    /**
     * Creates a new Trio instance from the specified values.
     *
     * @param values the array of values
     * @param <X>    the type of the values
     * @return a new Trio instance with the specified values
     */
    public static <X> Trio<X, X, X> from(final X[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("values must not be empty");
        }
        if (values.length != SIZE) {
            throw new IllegalArgumentException("values must have same size");
        }
        return new Trio<>(values[0], values[1], values[2]);
    }

    /**
     * Creates a new Trio instance from the specified values.
     *
     * @param values the list of values
     * @param <X>    the type of the values
     * @return a new Trio instance with the specified values
     */
    public static <X> Trio<X, X, X> from(final List<X> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("values must not be empty");
        }
        if (values.size() != SIZE) {
            throw new IllegalArgumentException("values must have same size");
        }
        return new Trio<>(values.get(0), values.get(1), values.get(2));
    }

    /**
     * Creates a new Trio instance with null values.
     *
     * @param <T1> the type of the first value
     * @param <T2> the type of the second value
     * @param <T3> the type of the third value
     * @return a new Trio instance with null values
     */
    public static <T1, T2, T3> Trio<T1, T2, T3> empty() {
        return new Trio<>(null, null, null);
    }

    @Override
    public int size() {
        return SIZE;
    }

    @Override
    public List<Object> values() {
        return List.of(value1, value2, value3);
    }
}
