package com.euonia.tuple;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SeptetTest {

    @Test
    void ofFromAndEmptyWork() {
        Septet<Integer, Integer, Integer, Integer, Integer, Integer, Integer> tuple = Septet.of(1, 2, 3, 4, 5, 6, 7);

        assertEquals(7, tuple.size());
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7), tuple.values());
        assertEquals(7, Septet.from(new Integer[]{1, 2, 3, 4, 5, 6, 7}).value7());
        assertEquals(3, Septet.from(List.of(1, 2, 3, 4, 5, 6, 7)).value3());
        assertNull(Septet.empty().value1());
    }

    @Test
    void fromRejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> Septet.from((Integer[]) null));
        assertThrows(IllegalArgumentException.class, () -> Septet.from(new Integer[]{}));
        assertThrows(IllegalArgumentException.class, () -> Septet.from(new Integer[]{1, 2, 3, 4, 5, 6}));
        assertThrows(IllegalArgumentException.class, () -> Septet.from((List<Integer>) null));
        assertThrows(IllegalArgumentException.class, () -> Septet.from(List.of()));
        assertThrows(IllegalArgumentException.class, () -> Septet.from(List.of(1, 2, 3, 4, 5, 6)));
    }
}

