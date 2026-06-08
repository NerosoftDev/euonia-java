package com.euonia.uow;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A unit of work that delegates all operations to a parent unit of work.
 *
 * <p>When {@link UnitOfWorkManager#begin} is called while a unit of work
 * is already active on the current thread (and {@code requiresNew} is
 * {@code false}), a {@code ChildUnitOfWork} is returned instead of a new
 * top-level unit. All lifecycle methods — save, commit, rollback, listener
 * registration — are forwarded to the parent.</p>
 *
 * <p>The {@link #completeAsync()} method is a no-op on a child, since
 * only the outermost unit of work should trigger completion.</p>
 *
 * @see UnitOfWork
 * @see UnitOfWorkManager
 */
public class ChildUnitOfWork extends UnitOfWork {
    private final UnitOfWork parent;

    public ChildUnitOfWork(UnitOfWork parent) {
        super(parent == null ? null : parent.getOptions());
        this.parent = Objects.requireNonNull(parent, "parent");
    }

    @Override
    public Map<String, Object> getItems() {
        return parent.getItems();
    }

    @Override
    public Map<String, UnitOfWorkContext> getContexts() {
        return parent.getContexts();
    }

    @Override
    public UnitOfWorkOptions getOptions() {
        return parent.getOptions();
    }

    @Override
    public UnitOfWork getOuter() {
        return parent.getOuter();
    }

    @Override
    public boolean isReserved() {
        return parent.isReserved();
    }

    @Override
    public String getReservationName() {
        return parent.getReservationName();
    }

    @Override
    public boolean isDisposed() {
        return parent.isDisposed();
    }

    @Override
    public boolean isCompleted() {
        return parent.isCompleted();
    }

    @Override
    public Throwable getFailure() {
        return parent.getFailure();
    }

    @Override
    public void addCompletedListener(Consumer<UnitOfWorkEvent> listener) {
        parent.addCompletedListener(listener);
    }

    @Override
    public void addFailedListener(Consumer<UnitOfWorkFailure> listener) {
        parent.addFailedListener(listener);
    }

    @Override
    public void addDisposedListener(Consumer<UnitOfWorkEvent> listener) {
        parent.addDisposedListener(listener);
    }

    @Override
    public void onCompleted(Supplier<CompletionStage<Void>> handler) {
        parent.onCompleted(handler);
    }

    @Override
    public void initialize(UnitOfWorkOptions options) {
        parent.initialize(options);
    }

    @Override
    public void reserve(String reservationName) {
        parent.reserve(reservationName);
    }

    @Override
    public CompletionStage<Void> saveChangesAsync() {
        return parent.saveChangesAsync();
    }

    @Override
    public CompletionStage<Void> rollbackAsync() {
        return parent.rollbackAsync();
    }

    @Override
    public CompletionStage<Void> completeAsync() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public UnitOfWorkContext findContext(String key) {
        return parent.findContext(key);
    }

    @Override
    public void addContext(String key, UnitOfWorkContext context) {
        parent.addContext(key, context);
    }

    @Override
    public UnitOfWorkContext getOrAddContext(String key, Supplier<UnitOfWorkContext> factory) {
        return parent.getOrAddContext(key, factory);
    }

    @Override
    public void setOuter(UnitOfWork outer) {
        parent.setOuter(outer);
    }

    @Override
    public void close() {
    }
}
