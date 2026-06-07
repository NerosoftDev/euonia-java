package com.euonia.tuple;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Solo")
class SoloTest {

    @Test
    @DisplayName("Given valid inputs when creating Solo then value is preserved")
    void givenValidInputsWhenCreatingSoloThenValueIsPreserved() {
        assertEquals("x", Solo.of("x").value());
        assertEquals("x", Solo.from(new String[]{"x"}).value());
        assertEquals("x", Solo.from(List.of("x")).value());
    }

    @Test
    @DisplayName("Given solo instance when querying size values and contains then expected behavior is observed")
    void givenSoloWhenQueryingCoreMethodsThenReturnExpectedResults() {
        Solo<String> solo = Solo.of("v");

        assertEquals(1, solo.size());
        assertEquals(List.of("v"), solo.values());
        assertTrue(solo.contains("v"));
        assertFalse(solo.contains("other"));
    }

    @Test
    @DisplayName("Given invalid inputs when creating Solo from array or list then illegal argument is thrown")
    void givenInvalidInputsWhenCreatingSoloFromArrayOrListThenThrowIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> Solo.from((String[]) null));
        assertThrows(IllegalArgumentException.class, () -> Solo.from(new String[]{}));
        assertThrows(IllegalArgumentException.class, () -> Solo.from(new String[]{"a", "b"}));
        assertThrows(IllegalArgumentException.class, () -> Solo.from((List<String>) null));
        assertThrows(IllegalArgumentException.class, () -> Solo.from(List.of()));
        assertThrows(IllegalArgumentException.class, () -> Solo.from(List.of("a", "b")));
    }
}

