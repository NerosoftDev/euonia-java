package com.euonia.tuple;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NonetTest {

    @Test
    void ofFromAndEmptyWork() {
        Nonet<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> tuple =
            Nonet.of(1, 2, 3, 4, 5, 6, 7, 8, 9);

        assertEquals(9, tuple.size());
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9), tuple.values());
        assertEquals(9, Nonet.from(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9}).value9());
        assertEquals(5, Nonet.from(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9)).value5());
        assertNull(Nonet.empty().value1());
    }

    @Test
    void fromRejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> Nonet.from((Integer[]) null));
        assertThrows(IllegalArgumentException.class, () -> Nonet.from(new Integer[]{}));
        assertThrows(IllegalArgumentException.class, () -> Nonet.from(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8}));
        assertThrows(IllegalArgumentException.class, () -> Nonet.from((List<Integer>) null));
        assertThrows(IllegalArgumentException.class, () -> Nonet.from(List.of()));
        assertThrows(IllegalArgumentException.class, () -> Nonet.from(List.of(1, 2, 3, 4, 5, 6, 7, 8)));
    }
}

