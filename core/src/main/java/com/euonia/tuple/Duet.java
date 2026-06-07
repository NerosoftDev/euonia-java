package com.euonia.tuple;

import java.io.Serial;
import java.util.List;

/**
 * Represents a tuple of two values, which can be of different types.
 * This class is immutable and provides methods to access the values and perform common tuple operations.
 *
 * @param value1 the first value
 * @param value2 the second value
 * @param <V1>   the type of the first value
 * @param <V2>   the type of the second value
 */
public record Duet<V1, V2>(V1 value1, V2 value2) implements Tuple {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final int SIZE = 2;

    /**
     * Creates a new instance of {@code Duet} with the given values.
     *
     * @param value1 the first value
     * @param value2 the second value
     * @param <V1>   the type of the first value
     * @param <V2>   the type of the second value
     * @return a new instance of {@code Duet} with the given values
     */
    public static <V1, V2> Duet<V1, V2> of(V1 value1, V2 value2) {
        return new Duet<>(value1, value2);
    }

    /**
     * Creates a new instance of {@code Duet} from the given values.
     *
     * @param values an array of values
     * @param <X>    the type of the values
     * @return a new instance of {@code Duet} with the given values
     */
    public static <X> Duet<X, X> from(final X[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("values must not be empty");
        }
        if (values.length != SIZE) {
            throw new IllegalArgumentException("values must have same size");
        }
        return new Duet<>(values[0], values[1]);
    }

    /**
     * Creates a new instance of {@code Duet} from the given values.
     *
     * @param values a list of values
     * @param <X>    the type of the values
     * @return a new instance of {@code Duet} with the given values
     */
    public static <X> Duet<X, X> from(final List<X> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("values must not be empty");
        }
        if (values.size() != SIZE) {
            throw new IllegalArgumentException("values must have same size");
        }
        return new Duet<>(values.get(0), values.get(1));
    }

    /**
     * Creates a new instance of {@code Duet} with null values.
     *
     * @param <V1> the type of the first value
     * @param <V2> the type of the second value
     * @return a new instance of {@code Duet} with null values
     */
    public static <V1, V2> Duet<V1, V2> empty() {
        return new Duet<>(null, null);
    }

    @Override
    public int size() {
        return SIZE;
    }

    @Override
    public List<Object> values() {
        return List.of(value1, value2);
    }
}
