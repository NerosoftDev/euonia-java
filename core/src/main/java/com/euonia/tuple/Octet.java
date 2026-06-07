package com.euonia.tuple;

import java.util.List;

/**
 * Represents a tuple of eight values, which can be of different types.
 * This class is immutable and provides methods to access the values and perform common tuple operations.
 *
 * @param value1 the first value
 * @param value2 the second value
 * @param value3 the third value
 * @param value4 the fourth value
 * @param value5 the fifth value
 * @param value6 the sixth value
 * @param value7 the seventh value
 * @param value8 the eighth value
 * @param <V1>   the type of the first value
 * @param <V2>   the type of the second value
 * @param <V3>   the type of the third value
 * @param <V4>   the type of the fourth value
 * @param <V5>   the type of the fifth value
 * @param <V6>   the type of the sixth value
 * @param <V7>   the type of the seventh value
 * @param <V8>   the type of the eighth value
 */
public record Octet<V1, V2, V3, V4, V5, V6, V7, V8>(V1 value1, V2 value2, V3 value3, V4 value4, V5 value5, V6 value6,
                                                    V7 value7, V8 value8) implements Tuple {
    private static final int SIZE = 8;

    /**
     * Creates a new Octet instance with the specified values.
     *
     * @param value1 the first value
     * @param value2 the second value
     * @param value3 the third value
     * @param value4 the fourth value
     * @param value5 the fifth value
     * @param value6 the sixth value
     * @param value7 the seventh value
     * @param value8 the eighth value
     * @param <V1>   the type of the first value
     * @param <V2>   the type of the second value
     * @param <V3>   the type of the third value
     * @param <V4>   the type of the fourth value
     * @param <V5>   the type of the fifth value
     * @param <V6>   the type of the sixth value
     * @param <V7>   the type of the seventh value
     * @param <V8>   the type of the eighth value
     * @return a new Octet instance with the specified values
     */
    public static <V1, V2, V3, V4, V5, V6, V7, V8> Octet<V1, V2, V3, V4, V5, V6, V7, V8> of(
        final V1 value1,
        final V2 value2,
        final V3 value3,
        final V4 value4,
        final V5 value5,
        final V6 value6,
        final V7 value7,
        final V8 value8) {
        return new Octet<>(value1, value2, value3, value4, value5, value6, value7, value8);
    }

    /**
     * Creates a new Octet instance from the specified array of values.
     *
     * @param values the array of values
     * @param <X>    the type of the values
     * @return a new Octet instance
     */
    public static <X> Octet<X, X, X, X, X, X, X, X> from(final X[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("values must not be empty");
        }
        if (values.length != SIZE) {
            throw new IllegalArgumentException("values must have same size");
        }
        return new Octet<>(values[0], values[1], values[2], values[3], values[4], values[5], values[6], values[7]);
    }

    /**
     * Creates a new Octet instance from the specified list of values.
     *
     * @param values the list of values
     * @param <X>    the type of the values
     * @return a new Octet instance
     */
    public static <X> Octet<X, X, X, X, X, X, X, X> from(final List<X> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("values must not be empty");
        }
        if (values.size() != SIZE) {
            throw new IllegalArgumentException("values must have same size");
        }
        return new Octet<>(values.get(0), values.get(1), values.get(2), values.get(3), values.get(4), values.get(5), values.get(6), values.get(7));
    }

    /**
     * Creates a new Octet instance with null values.
     *
     * @param <V1> the type of the first value
     * @param <V2> the type of the second value
     * @param <V3> the type of the third value
     * @param <V4> the type of the fourth value
     * @param <V5> the type of the fifth value
     * @param <V6> the type of the sixth value
     * @param <V7> the type of the seventh value
     * @param <V8> the type of the eighth value
     * @return a new Octet instance with all values set to null
     */
    public static <V1, V2, V3, V4, V5, V6, V7, V8> Octet<V1, V2, V3, V4, V5, V6, V7, V8> empty() {
        return new Octet<>(null, null, null, null, null, null, null, null);
    }

    @Override
    public int size() {
        return SIZE;
    }

    @Override
    public List<Object> values() {
        return List.of(value1, value2, value3, value4, value5, value6, value7, value8);
    }
}
