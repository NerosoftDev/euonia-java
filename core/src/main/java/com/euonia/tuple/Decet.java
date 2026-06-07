package com.euonia.tuple;

import java.util.List;

/**
 * Represents a tuple of ten values, which can be of different types.
 * This class is immutable and provides methods to access the values and perform common tuple operations.
 *
 * @param value1  the first value
 * @param value2  the second value
 * @param value3  the third value
 * @param value4  the fourth value
 * @param value5  the fifth value
 * @param value6  the sixth value
 * @param value7  the seventh value
 * @param value8  the eighth value
 * @param value9  the ninth value
 * @param value10 the tenth value
 * @param <V1>    the type of the first value
 * @param <V2>    the type of the second value
 * @param <V3>    the type of the third value
 * @param <V4>    the type of the fourth value
 * @param <V5>    the type of the fifth value
 * @param <V6>    the type of the sixth value
 * @param <V7>    the type of the seventh value
 * @param <V8>    the type of the eighth value
 * @param <V9>    the type of the ninth value
 * @param <V10>   the type of the tenth value
 */
public record Decet<V1, V2, V3, V4, V5, V6, V7, V8, V9, V10>(V1 value1, V2 value2, V3 value3, V4 value4, V5 value5,
                                                             V6 value6, V7 value7, V8 value8, V9 value9,
                                                             V10 value10) implements Tuple {
    private static final int SIZE = 10;

    /**
     * Creates a new Decet instance with the specified values.
     *
     * @param value1  the first value
     * @param value2  the second value
     * @param value3  the third value
     * @param value4  the fourth value
     * @param value5  the fifth value
     * @param value6  the sixth value
     * @param value7  the seventh value
     * @param value8  the eighth value
     * @param value9  the ninth value
     * @param value10 the tenth value
     * @param <V1>    the type of the first value
     * @param <V2>    the type of the second value
     * @param <V3>    the type of the third value
     * @param <V4>    the type of the fourth value
     * @param <V5>    the type of the fifth value
     * @param <V6>    the type of the sixth value
     * @param <V7>    the type of the seventh value
     * @param <V8>    the type of the eighth value
     * @param <V9>    the type of the ninth value
     * @param <V10>   the type of the tenth value
     * @return a new Decet instance with the specified values
     */
    public static <V1, V2, V3, V4, V5, V6, V7, V8, V9, V10> Decet<V1, V2, V3, V4, V5, V6, V7, V8, V9, V10> of(
        final V1 value1,
        final V2 value2,
        final V3 value3,
        final V4 value4,
        final V5 value5,
        final V6 value6,
        final V7 value7,
        final V8 value8,
        final V9 value9,
        final V10 value10) {
        return new Decet<>(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10);
    }

    /**
     * Creates a new Decet instance from an array of values.
     * The array must have exactly 10 elements, and the types of the elements will be inferred as the same type for all values.
     *
     * @param values the array of values
     * @param <X>    the type of the values
     * @return a new Decet instance with the specified values
     */
    public static <X> Decet<X, X, X, X, X, X, X, X, X, X> from(final X[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("values must not be empty");
        }
        if (values.length != SIZE) {
            throw new IllegalArgumentException("values must have same size");
        }
        return new Decet<>(values[0], values[1], values[2], values[3], values[4], values[5], values[6], values[7],
            values[8], values[9]);
    }

    /**
     * Creates a new Decet instance from a list of values.
     * The list must have exactly 10 elements, and the types of the elements will be inferred as the same type for all values.
     *
     * @param values the list of values
     * @param <X>    the type of the values
     * @return a new Decet instance with the specified values
     */
    public static <X> Decet<X, X, X, X, X, X, X, X, X, X> from(final List<X> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("values must not be empty");
        }
        if (values.size() != SIZE) {
            throw new IllegalArgumentException("values must have same size");
        }
        return new Decet<>(values.get(0), values.get(1), values.get(2), values.get(3), values.get(4), values.get(5),
            values.get(6), values.get(7), values.get(8), values.get(9));
    }

    /**
     * Creates an empty Decet instance with all values set to null.
     *
     * @param <V1>  the type of the first value
     * @param <V2>  the type of the second value
     * @param <V3>  the type of the third value
     * @param <V4>  the type of the fourth value
     * @param <V5>  the type of the fifth value
     * @param <V6>  the type of the sixth value
     * @param <V7>  the type of the seventh value
     * @param <V8>  the type of the eighth value
     * @param <V9>  the type of the ninth value
     * @param <V10> the type of the tenth value
     * @return a new Decet instance with all values set to null
     */
    public static <V1, V2, V3, V4, V5, V6, V7, V8, V9, V10> Decet<V1, V2, V3, V4, V5, V6, V7, V8, V9, V10> empty() {
        return new Decet<>(null, null, null, null, null, null, null, null, null, null);
    }

    @Override
    public int size() {
        return SIZE;
    }

    @Override
    public java.util.List<Object> values() {
        return java.util.List.of(value1, value2, value3, value4, value5, value6, value7, value8, value9, value10);
    }
}
