package com.euonia.tuple;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Septet")
class SeptetTest {

    @Test
    @DisplayName("Given valid inputs when creating Septet then values and empty factory behave as expected")
    void givenValidInputsWhenCreatingSeptetThenFactoriesBehaveAsExpected() {
        Septet<Integer, Integer, Integer, Integer, Integer, Integer, Integer> tuple = Septet.of(1, 2, 3, 4, 5, 6, 7);

        assertEquals(7, tuple.size());
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7), tuple.values());
        assertEquals(7, Septet.from(new Integer[]{1, 2, 3, 4, 5, 6, 7}).value7());
        assertEquals(3, Septet.from(List.of(1, 2, 3, 4, 5, 6, 7)).value3());
        assertNull(Septet.empty().value1());
    }

    @Test
    @DisplayName("Given invalid inputs when creating Septet from array or list then illegal argument is thrown")
    void givenInvalidInputsWhenCreatingSeptetFromArrayOrListThenThrowIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> Septet.from((Integer[]) null));
        assertThrows(IllegalArgumentException.class, () -> Septet.from(new Integer[]{}));
        assertThrows(IllegalArgumentException.class, () -> Septet.from(new Integer[]{1, 2, 3, 4, 5, 6}));
        assertThrows(IllegalArgumentException.class, () -> Septet.from((List<Integer>) null));
        assertThrows(IllegalArgumentException.class, () -> Septet.from(List.of()));
        assertThrows(IllegalArgumentException.class, () -> Septet.from(List.of(1, 2, 3, 4, 5, 6)));
    }
}

