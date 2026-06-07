package com.euonia.tuple;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuintetTest {

    @Test
    void ofFromAndEmptyWork() {
        Quintet<Integer, Integer, Integer, Integer, Integer> tuple = Quintet.of(1, 2, 3, 4, 5);

        assertEquals(5, tuple.size());
        assertEquals(List.of(1, 2, 3, 4, 5), tuple.values());
        assertEquals(5, Quintet.from(new Integer[]{1, 2, 3, 4, 5}).value5());
        assertEquals(1, Quintet.from(List.of(1, 2, 3, 4, 5)).value1());
        assertNull(Quintet.empty().value1());
    }

    @Test
    void fromRejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> Quintet.from((Integer[]) null));
        assertThrows(IllegalArgumentException.class, () -> Quintet.from(new Integer[]{}));
        assertThrows(IllegalArgumentException.class, () -> Quintet.from(new Integer[]{1, 2, 3, 4}));
        assertThrows(IllegalArgumentException.class, () -> Quintet.from((List<Integer>) null));
        assertThrows(IllegalArgumentException.class, () -> Quintet.from(List.of()));
        assertThrows(IllegalArgumentException.class, () -> Quintet.from(List.of(1, 2, 3, 4)));
    }
}

