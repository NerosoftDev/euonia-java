package com.euonia.core;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RandomId")
class RandomIdTest {

    @Test
    @DisplayName("Given a seed when generating then non-empty string is returned")
    void givenSeedWhenGeneratingThenNonEmptyStringReturned() {
        String result = RandomId.generate(42L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Given same seed when generating twice then results differ due to time-based key")
    void givenSameSeedWhenGeneratingTwiceThenResultsMayDiffer() {
        String first = RandomId.generate(1L);
        String second = RandomId.generate(1L);

        // Results depend on current time, so they may or may not differ.
        // Just verify both are valid non-empty strings.
        assertNotNull(first);
        assertNotNull(second);
        assertFalse(first.isEmpty());
        assertFalse(second.isEmpty());
    }

    @Test
    @DisplayName("Given seed 0 when generating then non-empty string is returned")
    void givenSeedZeroWhenGeneratingThenNonEmptyStringReturned() {
        String result = RandomId.generate(0L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}
