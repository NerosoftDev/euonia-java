package com.euonia.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ULID")
class ULIDTest {

    private static final String CROCKFORD_BASE32 = "0123456789ABCDEFGHJKMNPQRSTVWXYZ";

    @Test
    @DisplayName("Given generated ULID when checking format then it has 32 chars and valid alphabet")
    void givenGeneratedUlidWhenCheckingFormatThenLengthAndAlphabetAreValid() {
        String ulid = ULID.generate();

        assertNotNull(ulid);
        assertEquals(32, ulid.length());
        for (char c : ulid.toCharArray()) {
            assertTrue(CROCKFORD_BASE32.indexOf(c) >= 0, () -> "Unexpected character: " + c);
        }
    }

    @Test
    @DisplayName("Given multiple generated ULIDs when collecting then ids are unique")
    void givenMultipleGeneratedUlidsWhenCollectingThenIdsAreUnique() {
        Set<String> values = new HashSet<>();

        for (int i = 0; i < 200; i++) {
            values.add(ULID.generate());
        }

        assertEquals(200, values.size());
    }
}

