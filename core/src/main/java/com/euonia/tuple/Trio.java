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

    public static <T1, T2, T3> Trio<T1, T2, T3> of(T1 value1, T2 value2, T3 value3) {
        return new Trio<>(value1, value2, value3);
    }

    public static <X> Trio<X, X, X> of(final X[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("values must not be empty");
        }
        if (values.length != SIZE) {
            throw new IllegalArgumentException("values must have same size");
        }
        return new Trio<>(values[0], values[1], values[2]);
    }

    public static <X> Trio<X, X, X> of(final List<X> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("values must not be empty");
        }
        if (values.size() != SIZE) {
            throw new IllegalArgumentException("values must have same size");
        }
        return new Trio<>(values.get(0), values.get(1), values.get(2));
    }

    public static <T1, T2, T3> Trio<T1, T2, T3> empty() {
        return new Trio<>(null, null, null);
    }

    @Override
    public int size() {
        return SIZE;
    }

    @Override
    public Object value(int index) {
        return switch (index) {
            case 0 -> value1;
            case 1 -> value2;
            case 2 -> value3;
            default -> throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + SIZE);
        };
    }

    @Override
    public List<Object> values() {
        return List.of(value1, value2, value3);
    }

    @Override
    public boolean contains(Object value) {
        return values().contains(value);
    }
}
