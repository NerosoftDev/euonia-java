package com.euonia.tuple;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Sextet")
class SextetTest {

    @Test
    @DisplayName("Given valid inputs when creating Sextet then values and empty factory behave as expected")
    void givenValidInputsWhenCreatingSextetThenFactoriesBehaveAsExpected() {
        Sextet<Integer, Integer, Integer, Integer, Integer, Integer> tuple = Sextet.of(1, 2, 3, 4, 5, 6);

        assertEquals(6, tuple.size());
        assertEquals(List.of(1, 2, 3, 4, 5, 6), tuple.values());
        assertEquals(6, Sextet.from(new Integer[]{1, 2, 3, 4, 5, 6}).value6());
        assertEquals(2, Sextet.from(List.of(1, 2, 3, 4, 5, 6)).value2());
        assertNull(Sextet.empty().value1());
    }

    @Test
    @DisplayName("Given invalid inputs when creating Sextet from array or list then illegal argument is thrown")
    void givenInvalidInputsWhenCreatingSextetFromArrayOrListThenThrowIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> Sextet.from((Integer[]) null));
        assertThrows(IllegalArgumentException.class, () -> Sextet.from(new Integer[]{}));
        assertThrows(IllegalArgumentException.class, () -> Sextet.from(new Integer[]{1, 2, 3, 4, 5}));
        assertThrows(IllegalArgumentException.class, () -> Sextet.from((List<Integer>) null));
        assertThrows(IllegalArgumentException.class, () -> Sextet.from(List.of()));
        assertThrows(IllegalArgumentException.class, () -> Sextet.from(List.of(1, 2, 3, 4, 5)));
    }
}

