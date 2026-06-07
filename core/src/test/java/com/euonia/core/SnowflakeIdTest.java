package com.euonia.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SnowflakeId")
class SnowflakeIdTest {

    @Test
    @DisplayName("Given valid worker and datacenter when generating ids then ids are unique and increasing")
    void givenValidWorkerAndDatacenterWhenGeneratingThenIdsAreUniqueAndIncreasing() {
        SnowflakeId generator = SnowflakeId.getInstance(1, 1);

        long first = generator.nextId();
        long second = generator.nextId();

        assertTrue(first > 0);
        assertTrue(second > first);

        Set<Long> ids = new HashSet<>();
        for (int i = 0; i < 200; i++) {
            ids.add(generator.nextId());
        }
        assertEquals(200, ids.size());
    }

    @Test
    @DisplayName("Given default generator when generating ids then it returns positive ids")
    void givenDefaultGeneratorWhenGeneratingThenReturnPositiveIds() {
        SnowflakeId generator = SnowflakeId.getInstance();

        long id = generator.nextId();

        assertTrue(id > 0);
    }

    @Test
    @DisplayName("Given out of range worker or datacenter when creating generator then illegal argument is thrown")
    void givenOutOfRangeWorkerOrDatacenterWhenCreatingThenThrowIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> SnowflakeId.getInstance(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> SnowflakeId.getInstance(32, 0));
        assertThrows(IllegalArgumentException.class, () -> SnowflakeId.getInstance(0, -1));
        assertThrows(IllegalArgumentException.class, () -> SnowflakeId.getInstance(0, 32));
    }
}

