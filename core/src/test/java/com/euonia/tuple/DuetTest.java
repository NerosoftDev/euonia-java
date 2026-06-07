package com.euonia.tuple;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DuetTest {

    @Test
    void ofFromAndEmptyWork() {
        Duet<Integer, String> duet = Duet.of(1, "a");

        assertEquals(2, duet.size());
        assertEquals(List.of(1, "a"), duet.values());
        assertEquals(1, Duet.from(new Integer[]{1, 2}).value1());
        assertEquals(2, Duet.from(List.of(1, 2)).value2());
        assertNull(Duet.empty().value1());
        assertNull(Duet.empty().value2());
    }

    @Test
    void fromRejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> Duet.from((Integer[]) null));
        assertThrows(IllegalArgumentException.class, () -> Duet.from(new Integer[]{}));
        assertThrows(IllegalArgumentException.class, () -> Duet.from(new Integer[]{1}));
        assertThrows(IllegalArgumentException.class, () -> Duet.from((List<Integer>) null));
        assertThrows(IllegalArgumentException.class, () -> Duet.from(List.of()));
        assertThrows(IllegalArgumentException.class, () -> Duet.from(List.of(1)));
    }
}

