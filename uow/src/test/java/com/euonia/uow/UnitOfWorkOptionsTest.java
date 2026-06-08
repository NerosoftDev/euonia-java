package com.euonia.uow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.junit.jupiter.api.Test;

class UnitOfWorkOptionsTest {
    @Test
    void normalizeShouldFillMissingValues() {
        UnitOfWorkOptions defaults = new UnitOfWorkOptions(true, UnitOfWorkIsolationLevel.READ_COMMITTED, Duration.ofSeconds(30));
        UnitOfWorkOptions options = new UnitOfWorkOptions(false, null, null);

        UnitOfWorkOptions normalized = defaults.normalize(options);

        assertEquals(false, normalized.isTransactional());
        assertEquals(UnitOfWorkIsolationLevel.READ_COMMITTED, normalized.getIsolationLevel());
        assertEquals(Duration.ofSeconds(30), normalized.getTimeout());
    }
}