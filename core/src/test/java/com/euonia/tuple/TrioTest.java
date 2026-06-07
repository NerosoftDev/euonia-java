package com.euonia.tuple;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrioTest {

    @Test
    void ofFactoriesAndEmptyWork() {
        Trio<Integer, Integer, Integer> trio = Trio.of(1, 2, 3);

        assertEquals(3, trio.size());
        assertEquals(1, trio.value(0));
        assertEquals(2, trio.value(1));
        assertEquals(3, trio.value(2));
        assertTrue(trio.contains(2));
        assertEquals(List.of(1, 2, 3), trio.values());
        assertNull(Trio.empty().value1());
        assertEquals(3, Trio.from(new Integer[]{1, 2, 3}).value3());
        assertEquals(2, Trio.from(List.of(1, 2, 3)).value2());
    }

    @Test
    void valueAndFactoriesRejectInvalidInput() {
        Trio<Integer, Integer, Integer> trio = Trio.of(1, 2, 3);

        assertThrows(IndexOutOfBoundsException.class, () -> trio.value(3));
        assertThrows(IllegalArgumentException.class, () -> Trio.from((Integer[]) null));
        assertThrows(IllegalArgumentException.class, () -> Trio.from(new Integer[]{}));
        assertThrows(IllegalArgumentException.class, () -> Trio.from(new Integer[]{1, 2}));
        assertThrows(IllegalArgumentException.class, () -> Trio.from((List<Integer>) null));
        assertThrows(IllegalArgumentException.class, () -> Trio.from(List.of()));
        assertThrows(IllegalArgumentException.class, () -> Trio.from(List.of(1, 2)));
    }
}

