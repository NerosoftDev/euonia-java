package com.euonia.uow;

import java.sql.Connection;

public enum UnitOfWorkIsolationLevel {
    UNSPECIFIED(Connection.TRANSACTION_NONE),
    CHAOS(Connection.TRANSACTION_NONE),
    READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),
    READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),
    REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),
    SNAPSHOT(Connection.TRANSACTION_REPEATABLE_READ),
    SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);

    private final int jdbcIsolationLevel;

    UnitOfWorkIsolationLevel(int jdbcIsolationLevel) {
        this.jdbcIsolationLevel = jdbcIsolationLevel;
    }

    public int toJdbcIsolationLevel() {
        return jdbcIsolationLevel;
    }
}
