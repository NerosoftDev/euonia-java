package com.euonia.tuple;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DecetTest {

    @Test
    void ofFromAndEmptyWork() {
        Decet<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> tuple =
            Decet.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        assertEquals(10, tuple.size());
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), tuple.values());
        assertEquals(10, Decet.from(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}).value10());
        assertEquals(6, Decet.from(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)).value6());
        assertNull(Decet.empty().value1());
    }

    @Test
    void fromRejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> Decet.from((Integer[]) null));
        assertThrows(IllegalArgumentException.class, () -> Decet.from(new Integer[]{}));
        assertThrows(IllegalArgumentException.class, () -> Decet.from(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9}));
        assertThrows(IllegalArgumentException.class, () -> Decet.from((List<Integer>) null));
        assertThrows(IllegalArgumentException.class, () -> Decet.from(List.of()));
        assertThrows(IllegalArgumentException.class, () -> Decet.from(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9)));
    }
}

