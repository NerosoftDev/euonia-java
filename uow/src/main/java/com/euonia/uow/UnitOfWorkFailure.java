package com.euonia.uow;

/**
 * Event raised when a unit of work fails — either due to an exception
 * or an explicit rollback.
 *
 * <p>Listeners registered via {@link UnitOfWork#addFailedListener}
 * receive instances of this class.</p>
 *
 * @see UnitOfWorkEvent
 */
public class UnitOfWorkFailure extends UnitOfWorkEvent {
    private final Throwable exception;
    private final boolean rollback;

    /**
     * Creates a new failure event.
     *
     * @param unitOfWork the unit of work that failed
     * @param exception  the exception that caused the failure, may be {@code null}
     * @param rollback   whether a rollback was triggered
     */
    public UnitOfWorkFailure(UnitOfWork unitOfWork, Throwable exception, boolean rollback) {
        super(unitOfWork);
        this.exception = exception;
        this.rollback = rollback;
    }

    /**
     * Returns the exception that caused the failure, or {@code null}
     * if the failure was triggered by an explicit rollback.
     *
     * @return the exception, or {@code null}
     */
    public Throwable getException() {
        return exception;
    }

    /**
     * Returns whether a rollback was performed.
     *
     * @return {@code true} if the unit of work was rolled back
     */
    public boolean isRollback() {
        return rollback;
    }
}
