package com.euonia.uow;

public class UnitOfWorkEvent {
    private final UnitOfWork unitOfWork;

    public UnitOfWorkEvent(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    public UnitOfWork getUnitOfWork() {
        return unitOfWork;
    }
}
