package com.euonia.tuple;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SoloTest {

    @Test
    void ofAndFromProduceExpectedValue() {
        assertEquals("x", Solo.of("x").value());
        assertEquals("x", Solo.from(new String[]{"x"}).value());
        assertEquals("x", Solo.from(List.of("x")).value());
    }

    @Test
    void sizeValuesAndContainsWork() {
        Solo<String> solo = Solo.of("v");

        assertEquals(1, solo.size());
        assertEquals(List.of("v"), solo.values());
        assertTrue(solo.contains("v"));
        assertFalse(solo.contains("other"));
    }

    @Test
    void fromRejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> Solo.from((String[]) null));
        assertThrows(IllegalArgumentException.class, () -> Solo.from(new String[]{}));
        assertThrows(IllegalArgumentException.class, () -> Solo.from(new String[]{"a", "b"}));
        assertThrows(IllegalArgumentException.class, () -> Solo.from((List<String>) null));
        assertThrows(IllegalArgumentException.class, () -> Solo.from(List.of()));
        assertThrows(IllegalArgumentException.class, () -> Solo.from(List.of("a", "b")));
    }
}

