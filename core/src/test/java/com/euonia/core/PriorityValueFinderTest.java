package com.euonia.core;

import java.util.UUID;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PriorityValueFinder")
class PriorityValueFinderTest {

    @Test
    @DisplayName("Given queue and predicate when matching element exists then matching value is returned")
    void givenQueueAndPredicateWhenMatchExistsThenReturnMatch() {
        PriorityQueue<Supplier<Integer>, Integer> queue = new PriorityQueue<>();
        queue.add(() -> 3, 3);
        queue.add(() -> 1, 1);
        queue.add(() -> 2, 2);

        Integer result = PriorityValueFinder.find(queue, value -> value >= 2, -1);

        assertEquals(2, result);
    }

    @Test
    @DisplayName("Given queue and predicate when no element matches then default value is returned")
    void givenQueueAndPredicateWhenNoMatchThenReturnDefault() {
        PriorityQueue<Supplier<Integer>, Integer> queue = new PriorityQueue<>();
        queue.add(() -> 1, 1);
        queue.add(() -> 2, 2);

        Integer result = PriorityValueFinder.find(queue, value -> value > 10, -1);

        assertEquals(-1, result);
    }

    @Test
    @DisplayName("Given empty queue when finding then default value is returned")
    void givenEmptyQueueWhenFindingThenReturnDefault() {
        PriorityQueue<Supplier<Integer>, Integer> queue = new PriorityQueue<>();

        Integer result = PriorityValueFinder.find(queue, value -> true, 99);

        assertEquals(99, result);
    }

    @Test
    @DisplayName("Given supplier and consumer overloads when finding then expected value is returned")
    void givenSupplierAndConsumerOverloadsWhenFindingThenReturnExpectedValue() {
        Integer supplierResult = PriorityValueFinder.find(queue -> {
            queue.add(() -> 5, 5);
            queue.add(() -> 9, 9);
        }, value -> value > 6, -1);

        Integer consumerResult = PriorityValueFinder.find(queue -> {
            queue.add(() -> 8, 8);
            queue.add(() -> 4, 4);
        }, value -> value % 2 == 0 && value > 5, -1);

        assertEquals(9, supplierResult);
        assertEquals(8, consumerResult);
    }

    @Test
    void testComparator() {
        String random = RandomGenerator.getDefault().toString();
        String ulid = ObjectId.ulid().toString();
        String uuid = UUID.randomUUID().toString();
        String value = PriorityValueFinder.find(queue -> {
            queue.add(() -> "test", 1);
            queue.add(() -> ulid, 2);
            queue.add(() -> random, 3);
            queue.add(() -> uuid, 4);
        }, v -> v.length() > 20, "default");
        assertEquals(ulid, value);
    }

//    @Test
//    @DisplayName("Given invalid arguments when finding then illegal argument is thrown")
//    void givenInvalidArgumentsWhenFindingThenThrowIllegalArgument() {
//        assertThrows(IllegalArgumentException.class, () -> PriorityValueFinder.find((PriorityQueue<Boolean,Integer>) null, v -> true, 0));
//        assertThrows(IllegalArgumentException.class, () -> PriorityValueFinder.find(new PriorityQueue<Supplier<Integer>, Integer>(), null, 0));
//        assertThrows(IllegalArgumentException.class, () -> PriorityValueFinder.find((java.util.function.Supplier<PriorityQueue<Supplier<Integer>, Integer>>) null, v -> true, 0));
//        assertThrows(IllegalArgumentException.class, () -> PriorityValueFinder.find((java.util.function.Consumer<PriorityQueue<Supplier<Integer>, Integer>>) null, v -> true, 0));
//    }
}

