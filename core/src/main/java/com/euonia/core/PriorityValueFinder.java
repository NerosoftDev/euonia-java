package com.euonia.core;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Utility class for finding a value in a priority queue based on a filter predicate.
 * This class provides a method to search through a priority queue and return the first value that matches the given filter.
 * If no matching value is found, it returns a specified default value.
 */
public final class PriorityValueFinder {

    /**
     * Finds the first value in the priority queue that matches the given filter.
     * If no matching value is found, returns the default value.
     *
     * @param values       the priority queue to search
     * @param filter       the filter predicate to apply to each value
     * @param defaultValue the value to return if no matching value is found
     * @param <T>          the type of values in the priority queue
     * @return the first matching value or the default value if none is found
     */
    public static <T> T find(PriorityQueue<Supplier<T>, Integer> values, Predicate<T> filter, T defaultValue) {

        if (values == null) {
            throw new IllegalArgumentException("Values queue cannot be null  or empty");
        }

        if (filter == null) {
            throw new IllegalArgumentException("Filter queue cannot be null");
        }

        if (values.isEmpty()) {
            return defaultValue;
        }

        while (!values.isEmpty()) {
            T value = values.poll().get();
            if (filter.test(value)) {
                return value;
            }
        }
        return defaultValue;
    }

    /**
     * Finds the first value in the priority queue provided by the consumer that matches the given filter.
     * If no matching value is found, returns the default value.
     *
     * @param queueConsumer the consumer that provides the priority queue to search
     * @param filter        the filter predicate to apply to each value
     * @param defaultValue  the value to return if no matching value is found
     * @param <T>           the type of values in the priority queue
     * @return the first matching value or the default value if none is found
     */
    public static <T> T find(Consumer<PriorityQueue<Supplier<T>, Integer>> queueConsumer, Predicate<T> filter, T defaultValue) {
        if (queueConsumer == null) {
            throw new IllegalArgumentException("Queue consumer cannot be null");
        }

        var queue = new PriorityQueue<Supplier<T>, Integer>();
        queueConsumer.accept(queue);
        return find(queue, filter, defaultValue);
    }
}

