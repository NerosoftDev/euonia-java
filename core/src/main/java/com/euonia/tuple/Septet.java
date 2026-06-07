package com.euonia.tuple;

import java.io.Serial;
import java.util.List;

/**
 * Represents a tuple of seven values, which can be of different types.
 * This class is immutable and provides methods to access the values and perform common tuple operations.
 *
 * @param value1 the first value of the tuple
 * @param value2 the second value of the tuple
 * @param value3 the third value of the tuple
 * @param value4 the fourth value of the tuple
 * @param value5 the fifth value of the tuple
 * @param value6 the sixth value of the tuple
 * @param value7 the seventh value of the tuple
 * @param <V1>   the type of the first value
 * @param <V2>   the type of the second value
 * @param <V3>   the type of the third value
 * @param <V4>   the type of the fourth value
 * @param <V5>   the type of the fifth value
 * @param <V6>   the type of the sixth value
 * @param <V7>   the type of the seventh value
 */
public record Septet<V1, V2, V3, V4, V5, V6, V7>(V1 value1, V2 value2, V3 value3, V4 value4, V5 value5, V6 value6,
                                                 V7 value7) implements Tuple {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final int SIZE = 7;

    /**
     * Creates a new Septet instance with the specified values.
     *
     * @param value1 the first value of the tuple
     * @param value2 the second value of the tuple
     * @param value3 the third value of the tuple
     * @param value4 the fourth value of the tuple
     * @param value5 the fifth value of the tuple
     * @param value6 the sixth value of the tuple
     * @param value7 the seventh value of the tuple
     * @param <V1>   the type of the first value
     * @param <V2>   the type of the second value
     * @param <V3>   the type of the third value
     * @param <V4>   the type of the fourth value
     * @param <V5>   the type of the fifth value
     * @param <V6>   the type of the sixth value
     * @param <V7>   the type of the seventh value
     * @return a new Septet instance with the specified values
     */
    public static <V1, V2, V3, V4, V5, V6, V7> Septet<V1, V2, V3, V4, V5, V6, V7> of(V1 value1, V2 value2, V3 value3, V4 value4,
                                                                                     V5 value5, V6 value6, V7 value7) {
        return new Septet<>(value1, value2, value3, value4, value5, value6, value7);
    }

    /**
     * Creates a new Septet instance from the specified values.
     *
     * @param values the array of values
     * @param <X>    the type of the values
     * @return a new Septet instance with the specified values
     */
    public static <X> Septet<X, X, X, X, X, X, X> from(final X[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("values must not be empty");
        }
        if (values.length != SIZE) {
            throw new IllegalArgumentException("values must have same size");
        }
        return new Septet<>(values[0], values[1], values[2], values[3], values[4], values[5], values[6]);
    }

    /**
     * Creates a new Septet instance from the specified values.
     *
     * @param values the list of values
     * @param <X>    the type of the values
     * @return a new Septet instance with the specified values
     */
    public static <X> Septet<X, X, X, X, X, X, X> from(final List<X> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("values must not be empty");
        }
        if (values.size() != SIZE) {
            throw new IllegalArgumentException("values must have same size");
        }
        return new Septet<>(values.get(0), values.get(1), values.get(2), values.get(3), values.get(4), values.get(5), values.get(6));
    }

    /**
     * Creates a new Septet instance with all values set to null.
     *
     * @param <V1> the type of the first value
     * @param <V2> the type of the second value
     * @param <V3> the type of the third value
     * @param <V4> the type of the fourth value
     * @param <V5> the type of the fifth value
     * @param <V6> the type of the sixth value
     * @param <V7> the type of the seventh value
     * @return a new Septet instance with all values set to null
     */
    public static <V1, V2, V3, V4, V5, V6, V7> Septet<V1, V2, V3, V4, V5, V6, V7> empty() {
        return new Septet<>(null, null, null, null, null, null, null);
    }

    @Override
    public int size() {
        return SIZE;
    }

    @Override
    public List<Object> values() {
        return List.of(value1, value2, value3, value4, value5, value6, value7);
    }
}
