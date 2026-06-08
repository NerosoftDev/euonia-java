package com.euonia.uow;

/**
 * Thread-local holder for the current ambient {@link UnitOfWork}.
 *
 * <p>Each thread has its own independent unit of work, making this safe
 * for use in thread-per-request environments such as servlet containers.</p>
 *
 * <pre>{@code
 * UnitOfWork current = accessor.getCurrentUnitOfWork();
 * }</pre>
 */
public class UnitOfWorkAccessor {
    private final ThreadLocal<UnitOfWork> current = new ThreadLocal<>();

    /**
     * Returns the unit of work associated with the current thread,
     * or {@code null} if none is active.
     *
     * @return the current unit of work, or {@code null}
     */
    public UnitOfWork getCurrentUnitOfWork() {
        return current.get();
    }

    /**
     * Sets the unit of work for the current thread. Passing {@code null}
     * removes the association.
     *
     * @param unitOfWork the unit of work to associate, or {@code null} to clear
     */
    public void setCurrentUnitOfWork(UnitOfWork unitOfWork) {
        if (unitOfWork == null) {
            current.remove();
            return;
        }

        current.set(unitOfWork);
    }
}
