package com.euonia.uow;

public class UnitOfWorkFailure extends UnitOfWorkEvent {
    private final Throwable exception;
    private final boolean rollback;

    public UnitOfWorkFailure(UnitOfWork unitOfWork, Throwable exception, boolean rollback) {
        super(unitOfWork);
        this.exception = exception;
        this.rollback = rollback;
    }

    public Throwable getException() {
        return exception;
    }

    public boolean isRollback() {
        return rollback;
    }
}
