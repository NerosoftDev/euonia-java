package com.euonia.uow;

import java.time.Duration;

/**
 * Configuration options for a unit of work.
 *
 * <p>Options control transactional behavior, isolation level, and timeout.
 * Use {@link #normalize(UnitOfWorkOptions)} to merge with a set of defaults.</p>
 *
 * @see UnitOfWork
 * @see UnitOfWorkManager
 */
public class UnitOfWorkOptions {
    private boolean transactional;
    private UnitOfWorkIsolationLevel isolationLevel;
    private Duration timeout;

    /** Creates default, non-transactional options. */
    public UnitOfWorkOptions() {
    }

    /**
     * Creates options with the given transactional flag.
     *
     * @param transactional whether the unit of work is transactional
     */
    public UnitOfWorkOptions(boolean transactional) {
        this.transactional = transactional;
    }

    /**
     * Creates fully-specified options.
     *
     * @param transactional  whether the unit of work is transactional
     * @param isolationLevel the transaction isolation level, or {@code null}
     * @param timeout        the transaction timeout, or {@code null}
     */
    public UnitOfWorkOptions(boolean transactional, UnitOfWorkIsolationLevel isolationLevel, Duration timeout) {
        this.transactional = transactional;
        this.isolationLevel = isolationLevel;
        this.timeout = timeout;
    }

    /**
     * Returns whether the unit of work should be transactional.
     *
     * @return {@code true} if transactional
     */
    public boolean isTransactional() {
        return transactional;
    }

    public void setTransactional(boolean transactional) {
        this.transactional = transactional;
    }

    /**
     * Returns the transaction isolation level, or {@code null} if not set.
     *
     * @return the isolation level, or {@code null}
     */
    public UnitOfWorkIsolationLevel getIsolationLevel() {
        return isolationLevel;
    }

    public void setIsolationLevel(UnitOfWorkIsolationLevel isolationLevel) {
        this.isolationLevel = isolationLevel;
    }

    /**
     * Returns the transaction timeout, or {@code null} if not set.
     *
     * @return the timeout, or {@code null}
     */
    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    /**
     * Merges the given options with this instance, using this instance's
     * values as defaults for any {@code null} fields.
     *
     * @param options the options to normalize (must not be {@code null})
     * @return the normalized options object (the same instance)
     */
    public UnitOfWorkOptions normalize(UnitOfWorkOptions options) {
        if (options == null) {
            throw new IllegalArgumentException("options");
        }

        if (options.isolationLevel == null) {
            options.isolationLevel = isolationLevel;
        }

        if (options.timeout == null) {
            options.timeout = timeout;
        }

        return options;
    }
}
