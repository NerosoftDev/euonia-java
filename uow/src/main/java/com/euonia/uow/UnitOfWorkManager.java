package com.euonia.uow;

import java.util.Objects;

/**
 * Entry point for creating and managing units of work.
 *
 * <p>Each manager holds a default set of {@link UnitOfWorkOptions} and
 * a {@link UnitOfWorkAccessor} for tracking the ambient unit of work on
 * the current thread.</p>
 *
 * <pre>{@code
 * UnitOfWorkManager manager = new UnitOfWorkManager();
 * try (UnitOfWork uow = manager.begin(new UnitOfWorkOptions(true), false)) {
 *     uow.addContext("db", new JdbcTransactionContext(connection));
 *     // ... business logic ...
 *     uow.completeAsync().toCompletableFuture().join();
 * }
 * }</pre>
 *
 * @see UnitOfWork
 * @see ChildUnitOfWork
 */
public class UnitOfWorkManager {
    private final UnitOfWorkAccessor accessor;
    private final UnitOfWorkOptions defaultOptions;

    /** Creates a manager with default options and a new accessor. */
    public UnitOfWorkManager() {
        this(new UnitOfWorkAccessor(), new UnitOfWorkOptions());
    }

    /**
     * Creates a manager with the given accessor and default options.
     *
     * @param accessor       the accessor for tracking the current unit of work
     * @param defaultOptions the default options to apply to new units of work
     */
    public UnitOfWorkManager(UnitOfWorkAccessor accessor, UnitOfWorkOptions defaultOptions) {
        this.accessor = Objects.requireNonNull(accessor, "accessor");
        this.defaultOptions = defaultOptions == null ? new UnitOfWorkOptions() : defaultOptions;
    }

    /**
     * Returns the unit of work associated with the current thread,
     * or {@code null} if none is active.
     *
     * @return the current unit of work, or {@code null}
     */
    public UnitOfWork getCurrent() {
        return accessor.getCurrentUnitOfWork();
    }

    /**
     * Begins a new unit of work with the given options.
     *
     * <p>If a unit of work is already active on the current thread and
     * {@code requiresNew} is {@code false}, a {@link ChildUnitOfWork}
     * that delegates to the existing unit is returned. Otherwise a new
     * top-level unit is created and set as the ambient unit of work.</p>
     *
     * @param options     the options for the unit of work
     * @param requiresNew if {@code true}, always creates a new top-level unit
     * @return the unit of work (must be closed via {@link UnitOfWork#close()})
     */
    public UnitOfWork begin(UnitOfWorkOptions options, boolean requiresNew) {
        if (options == null) {
            throw new IllegalArgumentException("options");
        }

        UnitOfWork current = getCurrent();
        if (current != null && !requiresNew) {
            return new ChildUnitOfWork(current);
        }

        UnitOfWork unitOfWork = new UnitOfWork(defaultOptions);
        unitOfWork.initialize(options);
        unitOfWork.setOuter(current);
        accessor.setCurrentUnitOfWork(unitOfWork);
        unitOfWork.addDisposedListener(event -> accessor.setCurrentUnitOfWork(current));
        return unitOfWork;
    }

    /**
     * Convenience method for beginning a unit of work with a
     * transactional flag.
     *
     * @param transactional whether the unit of work is transactional
     * @param requiresNew   if {@code true}, always creates a new top-level unit
     * @return the unit of work
     */
    public UnitOfWork begin(boolean transactional, boolean requiresNew) {
        return begin(new UnitOfWorkOptions(transactional), requiresNew);
    }
}
