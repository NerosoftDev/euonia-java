package com.euonia.tuple;

import java.io.Serial;
import java.util.List;

/**
 * Represents a tuple of six values, which can be of different types.
 * This class is immutable and provides methods to access the values and perform common tuple operations.
 *
 * @param value1 the first value of the tuple
 * @param value2 the second value of the tuple
 * @param value3 the third value of the tuple
 * @param value4 the fourth value of the tuple
 * @param value5 the fifth value of the tuple
 * @param value6 the sixth value of the tuple
 * @param <V1>   the type of the first value
 * @param <V2>   the type of the second value
 * @param <V3>   the type of the third value
 * @param <V4>   the type of the fourth value
 * @param <V5>   the type of the fifth value
 * @param <V6>   the type of the sixth value
 */
public record Sextet<V1, V2, V3, V4, V5, V6>(V1 value1, V2 value2, V3 value3, V4 value4, V5 value5,
                                             V6 value6) implements Tuple {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final int SIZE = 6;

    /**
     * Creates a new Sextet instance with the specified values.
     *
     * @param value1 the first value of the tuple
     * @param value2 the second value of the tuple
     * @param value3 the third value of the tuple
     * @param value4 the fourth value of the tuple
     * @param value5 the fifth value of the tuple
     * @param value6 the sixth value of the tuple
     * @param <V1>   the type of the first value
     * @param <V2>   the type of the second value
     * @param <V3>   the type of the third value
     * @param <V4>   the type of the fourth value
     * @param <V5>   the type of the fifth value
     * @param <V6>   the type of the sixth value
     * @return a new Sextet instance with the specified values
     */
    public static <V1, V2, V3, V4, V5, V6> Sextet<V1, V2, V3, V4, V5, V6> of(V1 value1, V2 value2, V3 value3, V4 value4, V5 value5, V6 value6) {
        return new Sextet<>(value1, value2, value3, value4, value5, value6);
    }

    /**
     * Creates a new Sextet instance from the specified values.
     *
     * @param values the array of values
     * @param <X>    the type of the values
     * @return a new Sextet instance with the specified values
     */
    public static <X> Sextet<X, X, X, X, X, X> from(final X[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("values must not be empty");
        }
        if (values.length != SIZE) {
            throw new IllegalArgumentException("values must have same size");
        }
        return new Sextet<>(values[0], values[1], values[2], values[3], values[4], values[5]);
    }

    /**
     * Creates a new Sextet instance from the specified values.
     *
     * @param values the list of values
     * @param <X>    the type of the values
     * @return a new Sextet instance with the specified values
     */
    public static <X> Sextet<X, X, X, X, X, X> from(final List<X> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("values must not be empty");
        }
        if (values.size() != SIZE) {
            throw new IllegalArgumentException("values must have same size");
        }
        return new Sextet<>(values.get(0), values.get(1), values.get(2), values.get(3), values.get(4), values.get(5));
    }

    public static <V1, V2, V3, V4, V5, V6> Sextet<V1, V2, V3, V4, V5, V6> empty() {
        return new Sextet<>(null, null, null, null, null, null);
    }

    @Override
    public int size() {
        return SIZE;
    }

    @Override
    public java.util.List<Object> values() {
        return java.util.List.of(value1, value2, value3, value4, value5, value6);
    }
}
