package com.euonia.uow;

import java.util.Objects;

public class UnitOfWorkManager {
    private final UnitOfWorkAccessor accessor;
    private final UnitOfWorkOptions defaultOptions;

    public UnitOfWorkManager() {
        this(new UnitOfWorkAccessor(), new UnitOfWorkOptions());
    }

    public UnitOfWorkManager(UnitOfWorkAccessor accessor, UnitOfWorkOptions defaultOptions) {
        this.accessor = Objects.requireNonNull(accessor, "accessor");
        this.defaultOptions = defaultOptions == null ? new UnitOfWorkOptions() : defaultOptions;
    }

    public UnitOfWork getCurrent() {
        return accessor.getCurrentUnitOfWork();
    }

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

    public UnitOfWork begin(boolean transactional, boolean requiresNew) {
        return begin(new UnitOfWorkOptions(transactional), requiresNew);
    }
}
