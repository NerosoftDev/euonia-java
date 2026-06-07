package com.euonia.tuple;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Octet")
class OctetTest {

    @Test
    @DisplayName("Given valid inputs when creating Octet then values and empty factory behave as expected")
    void givenValidInputsWhenCreatingOctetThenFactoriesBehaveAsExpected() {
        Octet<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> tuple = Octet.of(1, 2, 3, 4, 5, 6, 7, 8);

        assertEquals(8, tuple.size());
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8), tuple.values());
        assertEquals(8, Octet.from(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8}).value8());
        assertEquals(4, Octet.from(List.of(1, 2, 3, 4, 5, 6, 7, 8)).value4());
        assertNull(Octet.empty().value1());
    }

    @Test
    @DisplayName("Given invalid inputs when creating Octet from array or list then illegal argument is thrown")
    void givenInvalidInputsWhenCreatingOctetFromArrayOrListThenThrowIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> Octet.from((Integer[]) null));
        assertThrows(IllegalArgumentException.class, () -> Octet.from(new Integer[]{}));
        assertThrows(IllegalArgumentException.class, () -> Octet.from(new Integer[]{1, 2, 3, 4, 5, 6, 7}));
        assertThrows(IllegalArgumentException.class, () -> Octet.from((List<Integer>) null));
        assertThrows(IllegalArgumentException.class, () -> Octet.from(List.of()));
        assertThrows(IllegalArgumentException.class, () -> Octet.from(List.of(1, 2, 3, 4, 5, 6, 7)));
    }
}

