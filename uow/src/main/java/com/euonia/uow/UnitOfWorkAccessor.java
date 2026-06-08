package com.euonia.uow;

public class UnitOfWorkAccessor {
    private final ThreadLocal<UnitOfWork> current = new ThreadLocal<>();

    public UnitOfWork getCurrentUnitOfWork() {
        return current.get();
    }

    public void setCurrentUnitOfWork(UnitOfWork unitOfWork) {
        if (unitOfWork == null) {
            current.remove();
            return;
        }

        current.set(unitOfWork);
    }
}
