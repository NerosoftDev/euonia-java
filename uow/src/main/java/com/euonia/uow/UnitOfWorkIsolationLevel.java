package com.euonia.uow;

import java.sql.Connection;

/**
 * Transaction isolation levels, compatible with JDBC isolation constants.
 *
 * <ul>
 *   <li>{@link #UNSPECIFIED} — use the underlying store's default</li>
 *   <li>{@link #READ_UNCOMMITTED} — dirty reads allowed</li>
 *   <li>{@link #READ_COMMITTED} — no dirty reads (most common)</li>
 *   <li>{@link #REPEATABLE_READ} — consistent reads within a transaction</li>
 *   <li>{@link #SERIALIZABLE} — strictest isolation</li>
 * </ul>
 *
 * @see java.sql.Connection#TRANSACTION_NONE
 * @see java.sql.Connection#TRANSACTION_READ_UNCOMMITTED
 * @see java.sql.Connection#TRANSACTION_READ_COMMITTED
 * @see java.sql.Connection#TRANSACTION_REPEATABLE_READ
 * @see java.sql.Connection#TRANSACTION_SERIALIZABLE
 */
public enum UnitOfWorkIsolationLevel {
    /** Use the underlying store's default isolation level. */
    UNSPECIFIED(Connection.TRANSACTION_NONE),
    /** Chaos isolation — maps to {@link Connection#TRANSACTION_NONE}. */
    CHAOS(Connection.TRANSACTION_NONE),
    /** Dirty reads, non-repeatable reads, and phantom reads may occur. */
    READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),
    /** Prevents dirty reads; non-repeatable and phantom reads may occur. */
    READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),
    /** Prevents dirty and non-repeatable reads; phantom reads may occur. */
    REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),
    /** Snapshot isolation — maps to {@link Connection#TRANSACTION_REPEATABLE_READ}. */
    SNAPSHOT(Connection.TRANSACTION_REPEATABLE_READ),
    /** Prevents dirty reads, non-repeatable reads, and phantom reads. */
    SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);

    private final int jdbcIsolationLevel;

    UnitOfWorkIsolationLevel(int jdbcIsolationLevel) {
        this.jdbcIsolationLevel = jdbcIsolationLevel;
    }

    /**
     * Converts this isolation level to the corresponding JDBC constant.
     *
     * @return a {@link Connection} transaction isolation constant
     */
    public int toJdbcIsolationLevel() {
        return jdbcIsolationLevel;
    }
}
