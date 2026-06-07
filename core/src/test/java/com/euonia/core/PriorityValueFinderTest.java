package com.euonia.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PriorityValueFinder")
class PriorityValueFinderTest {

    @Test
    @DisplayName("Given queue and predicate when matching element exists then matching value is returned")
    void givenQueueAndPredicateWhenMatchExistsThenReturnMatch() {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        queue.add(3);
        queue.add(1);
        queue.add(2);

        Integer result = PriorityValueFinder.find(queue, value -> value >= 2, -1);

        assertEquals(2, result);
    }

    @Test
    @DisplayName("Given queue and predicate when no element matches then default value is returned")
    void givenQueueAndPredicateWhenNoMatchThenReturnDefault() {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        queue.add(1);
        queue.add(2);

        Integer result = PriorityValueFinder.find(queue, value -> value > 10, -1);

        assertEquals(-1, result);
    }

    @Test
    @DisplayName("Given empty queue when finding then default value is returned")
    void givenEmptyQueueWhenFindingThenReturnDefault() {
        PriorityQueue<Integer> queue = new PriorityQueue<>();

        Integer result = PriorityValueFinder.find(queue, value -> true, 99);

        assertEquals(99, result);
    }

    @Test
    @DisplayName("Given supplier and consumer overloads when finding then expected value is returned")
    void givenSupplierAndConsumerOverloadsWhenFindingThenReturnExpectedValue() {
        Integer supplierResult = PriorityValueFinder.find(() -> {
            PriorityQueue<Integer> queue = new PriorityQueue<>();
            queue.add(5);
            queue.add(9);
            return queue;
        }, value -> value > 6, -1);

        Integer consumerResult = PriorityValueFinder.find(queue -> {
            queue.add(8);
            queue.add(4);
        }, value -> value % 2 == 0 && value > 5, -1);

        assertEquals(9, supplierResult);
        assertEquals(8, consumerResult);
    }

    @Test
    @DisplayName("Given invalid arguments when finding then illegal argument is thrown")
    void givenInvalidArgumentsWhenFindingThenThrowIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> PriorityValueFinder.find((PriorityQueue<Integer>) null, v -> true, 0));
        assertThrows(IllegalArgumentException.class, () -> PriorityValueFinder.find(new PriorityQueue<Integer>(), null, 0));
        assertThrows(IllegalArgumentException.class, () -> PriorityValueFinder.find((java.util.function.Supplier<PriorityQueue<Integer>>) null, v -> true, 0));
        assertThrows(IllegalArgumentException.class, () -> PriorityValueFinder.find((java.util.function.Consumer<PriorityQueue<Integer>>) null, v -> true, 0));
    }
}

