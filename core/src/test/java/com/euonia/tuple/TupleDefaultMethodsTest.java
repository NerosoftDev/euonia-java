package com.euonia.tuple;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tuple default methods")
class TupleDefaultMethodsTest {

    @Test
    @DisplayName("Given tuple values when using common default methods then expected results are returned")
    void givenTupleValuesWhenUsingCommonDefaultsThenReturnExpectedResults() {
        Tuple tuple = Trio.of(1, 2, 1);

        assertEquals(3, tuple.size());
        assertEquals(1, tuple.value(0));
        assertTrue(tuple.contains(2));
        assertTrue(tuple.containsAll(List.of(1, 2)));
        assertTrue(tuple.containsAll(1, 2));
        assertFalse(tuple.containsAll(1, 3));
        assertTrue(tuple.containsAny(List.of(9, 2)));
        assertTrue(tuple.containsAny(9, 1));
        assertFalse(tuple.containsAny(9, 8));
        assertEquals(0, tuple.indexOf(1));
        assertEquals(2, tuple.lastIndexOf(1));
        assertArrayEquals(new Object[]{1, 2, 1}, tuple.toArray());
        assertArrayEquals(new Integer[]{1, 2, 1}, tuple.toArray(new Integer[0]));
        assertEquals(List.of(1, 2, 1), tuple.toList());
    }

    @Test
    @DisplayName("Given duplicated elements when comparing ignoring order then multiplicity and size are respected")
    void givenDuplicatedElementsWhenComparingIgnoreOrderThenRespectMultiplicityAndSize() {
        Tuple left = Quartet.of(1, 2, 2, 3);
        Tuple sameElementsDifferentOrder = Quartet.of(2, 3, 1, 2);
        Tuple missingDuplicate = Quartet.of(1, 2, 3, 3);
        Tuple differentSize = Trio.of(1, 2, 2);

        assertTrue(left.equalsIgnoreOrder(sameElementsDifferentOrder));
        assertFalse(left.equalsIgnoreOrder(missingDuplicate));
        assertFalse(left.equalsIgnoreOrder(differentSize));
        assertFalse(left.equalsIgnoreOrder(null));
    }

    @Test
    @DisplayName("Given invalid index when reading tuple value then index out of bounds is thrown")
    void givenInvalidIndexWhenReadingValueThenThrowIndexOutOfBounds() {
        Tuple tuple = Duet.of(1, 2);

        assertThrows(IndexOutOfBoundsException.class, () -> tuple.value(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> tuple.value(2));
    }
}

