package com.euonia.tuple;

import java.io.Serial;
import java.util.List;

/**
 * Represents a tuple of four values, which can be of different types.
 * This class is immutable and provides methods to access the values and perform common tuple operations.
 *
 * @param value1 the first value of the tuple
 * @param value2 the second value of the tuple
 * @param value3 the third value of the tuple
 * @param value4 the fourth value of the tuple
 * @param <V1>   the type of the first value
 * @param <V2>   the type of the second value
 * @param <V3>   the type of the third value
 * @param <V4>   the type of the fourth value
 */
public record Quartet<V1, V2, V3, V4>(V1 value1, V2 value2, V3 value3, V4 value4) implements Tuple {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final int SIZE = 4;

    /**
     * Creates a new Quartet instance with the specified values.
     *
     * @param value1 the first value of the tuple
     * @param value2 the second value of the tuple
     * @param value3 the third value of the tuple
     * @param value4 the fourth value of the tuple
     * @param <V1>   the type of the first value
     * @param <V2>   the type of the second value
     * @param <V3>   the type of the third value
     * @param <V4>   the type of the fourth value
     * @return a new Quartet instance with the specified values
     */
    public static <V1, V2, V3, V4> Quartet<V1, V2, V3, V4> of(V1 value1, V2 value2, V3 value3, V4 value4) {
        return new Quartet<>(value1, value2, value3, value4);
    }

    /**
     * Creates a new Quartet instance from the specified values.
     *
     * @param values the array of values
     * @param <X>    the type of the values
     * @return a new Quartet instance with the specified values
     */
    public static <X> Quartet<X, X, X, X> from(final X[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("values must not be empty");
        }
        if (values.length != SIZE) {
            throw new IllegalArgumentException("values must have same size");
        }
        return new Quartet<>(values[0], values[1], values[2], values[3]);
    }

    /**
     * Creates a new Quartet instance from the specified values.
     *
     * @param values the list of values
     * @param <X>    the type of the values
     * @return a new Quartet instance with the specified values
     */
    public static <X> Quartet<X, X, X, X> from(final List<X> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("values must not be empty");
        }
        if (values.size() != SIZE) {
            throw new IllegalArgumentException("values must have same size");
        }
        return new Quartet<>(values.get(0), values.get(1), values.get(2), values.get(3));
    }

    /**
     * Creates an empty Quartet instance with all values set to null.
     *
     * @param <V1> the type of the first value
     * @param <V2> the type of the second value
     * @param <V3> the type of the third value
     * @param <V4> the type of the fourth value
     * @return a new Quartet instance with all values set to null
     */
    public static <V1, V2, V3, V4> Quartet<V1, V2, V3, V4> empty() {
        return new Quartet<>(null, null, null, null);
    }

    @Override
    public int size() {
        return SIZE;
    }

    @Override
    public List<Object> values() {
        return List.of(value1, value2, value3, value4);
    }
}
