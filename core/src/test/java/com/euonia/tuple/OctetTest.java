package com.euonia.tuple;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OctetTest {

    @Test
    void ofFromAndEmptyWork() {
        Octet<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> tuple = Octet.of(1, 2, 3, 4, 5, 6, 7, 8);

        assertEquals(8, tuple.size());
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8), tuple.values());
        assertEquals(8, Octet.from(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8}).value8());
        assertEquals(4, Octet.from(List.of(1, 2, 3, 4, 5, 6, 7, 8)).value4());
        assertNull(Octet.empty().value1());
    }

    @Test
    void fromRejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> Octet.from((Integer[]) null));
        assertThrows(IllegalArgumentException.class, () -> Octet.from(new Integer[]{}));
        assertThrows(IllegalArgumentException.class, () -> Octet.from(new Integer[]{1, 2, 3, 4, 5, 6, 7}));
        assertThrows(IllegalArgumentException.class, () -> Octet.from((List<Integer>) null));
        assertThrows(IllegalArgumentException.class, () -> Octet.from(List.of()));
        assertThrows(IllegalArgumentException.class, () -> Octet.from(List.of(1, 2, 3, 4, 5, 6, 7)));
    }
}

