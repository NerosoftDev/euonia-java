package com.euonia.uow;

/**
 * Event raised when a unit of work completes or is disposed.
 *
 * <p>Listeners registered via {@link UnitOfWork#addCompletedListener}
 * and {@link UnitOfWork#addDisposedListener} receive instances of this class.</p>
 *
 * @see UnitOfWorkFailure
 */
public class UnitOfWorkEvent {
    private final UnitOfWork unitOfWork;

    /**
     * Creates a new event for the given unit of work.
     *
     * @param unitOfWork the unit of work that triggered the event
     */
    public UnitOfWorkEvent(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    /**
     * Returns the unit of work that triggered this event.
     *
     * @return the associated unit of work
     */
    public UnitOfWork getUnitOfWork() {
        return unitOfWork;
    }
}
