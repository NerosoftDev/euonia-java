package com.euonia.tuple;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuartetTest {

    @Test
    void ofFromAndEmptyWork() {
        Quartet<Integer, Integer, Integer, Integer> tuple = Quartet.of(1, 2, 3, 4);

        assertEquals(4, tuple.size());
        assertEquals(List.of(1, 2, 3, 4), tuple.values());
        assertEquals(3, Quartet.from(new Integer[]{1, 2, 3, 4}).value3());
        assertEquals(4, Quartet.from(List.of(1, 2, 3, 4)).value4());
        assertNull(Quartet.empty().value1());
    }

    @Test
    void fromRejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> Quartet.from((Integer[]) null));
        assertThrows(IllegalArgumentException.class, () -> Quartet.from(new Integer[]{}));
        assertThrows(IllegalArgumentException.class, () -> Quartet.from(new Integer[]{1, 2, 3}));
        assertThrows(IllegalArgumentException.class, () -> Quartet.from((List<Integer>) null));
        assertThrows(IllegalArgumentException.class, () -> Quartet.from(List.of()));
        assertThrows(IllegalArgumentException.class, () -> Quartet.from(List.of(1, 2, 3)));
    }
}

