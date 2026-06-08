package com.euonia.uow;

import java.time.Duration;

public class UnitOfWorkOptions {
    private boolean transactional;
    private UnitOfWorkIsolationLevel isolationLevel;
    private Duration timeout;

    public UnitOfWorkOptions() {
    }

    public UnitOfWorkOptions(boolean transactional) {
        this.transactional = transactional;
    }

    public UnitOfWorkOptions(boolean transactional, UnitOfWorkIsolationLevel isolationLevel, Duration timeout) {
        this.transactional = transactional;
        this.isolationLevel = isolationLevel;
        this.timeout = timeout;
    }

    public boolean isTransactional() {
        return transactional;
    }

    public void setTransactional(boolean transactional) {
        this.transactional = transactional;
    }

    public UnitOfWorkIsolationLevel getIsolationLevel() {
        return isolationLevel;
    }

    public void setIsolationLevel(UnitOfWorkIsolationLevel isolationLevel) {
        this.isolationLevel = isolationLevel;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

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
