package com.euonia.uow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class UnitOfWork implements AutoCloseable {
    private final String id = UUID.randomUUID().toString();
    private final Map<String, Object> items = new ConcurrentHashMap<>();
    private final Map<String, UnitOfWorkContext> contexts = new ConcurrentHashMap<>();
    private final List<Supplier<CompletionStage<Void>>> completedHandlers = new CopyOnWriteArrayList<>();
    private final List<Consumer<UnitOfWorkEvent>> completedListeners = new CopyOnWriteArrayList<>();
    private final List<Consumer<UnitOfWorkFailure>> failedListeners = new CopyOnWriteArrayList<>();
    private final List<Consumer<UnitOfWorkEvent>> disposedListeners = new CopyOnWriteArrayList<>();

    private final UnitOfWorkOptions defaultOptions;

    private volatile boolean completing;
    private volatile boolean completed;
    private volatile boolean disposed;
    private volatile boolean rolledBack;
    private volatile Throwable failure;

    private UnitOfWorkOptions options;
    private UnitOfWork outer;
    private boolean reserved;
    private String reservationName;

    public UnitOfWork() {
        this(new UnitOfWorkOptions());
    }

    public UnitOfWork(UnitOfWorkOptions defaultOptions) {
        this.defaultOptions = defaultOptions == null ? new UnitOfWorkOptions() : defaultOptions;
    }

    public String getId() {
        return id;
    }

    public Map<String, Object> getItems() {
        return items;
    }

    public Map<String, UnitOfWorkContext> getContexts() {
        return Collections.unmodifiableMap(contexts);
    }

    public UnitOfWorkOptions getOptions() {
        return options;
    }

    public UnitOfWork getOuter() {
        return outer;
    }

    public boolean isReserved() {
        return reserved;
    }

    public String getReservationName() {
        return reservationName;
    }

    public boolean isDisposed() {
        return disposed;
    }

    public boolean isCompleted() {
        return completed;
    }

    public Throwable getFailure() {
        return failure;
    }

    public void addCompletedListener(Consumer<UnitOfWorkEvent> listener) {
        completedListeners.add(Objects.requireNonNull(listener, "listener"));
    }

    public void addFailedListener(Consumer<UnitOfWorkFailure> listener) {
        failedListeners.add(Objects.requireNonNull(listener, "listener"));
    }

    public void addDisposedListener(Consumer<UnitOfWorkEvent> listener) {
        disposedListeners.add(Objects.requireNonNull(listener, "listener"));
    }

    public void onCompleted(Supplier<CompletionStage<Void>> handler) {
        completedHandlers.add(Objects.requireNonNull(handler, "handler"));
    }

    public void initialize(UnitOfWorkOptions options) {
        if (options == null) {
            throw new IllegalArgumentException("options");
        }

        if (this.options != null) {
            throw new IllegalStateException("This unit of work is already initialized before!");
        }

        this.options = defaultOptions.normalize(options);
        this.reserved = false;
    }

    public void reserve(String reservationName) {
        if (reservationName == null || reservationName.isBlank()) {
            throw new IllegalArgumentException("reservationName");
        }

        this.reservationName = reservationName;
        this.reserved = true;
    }

    public CompletionStage<Void> saveChangesAsync() {
        if (rolledBack) {
            return CompletableFuture.completedFuture(null);
        }

        List<CompletionStage<Void>> stages = new ArrayList<>();
        for (UnitOfWorkContext context : contexts.values()) {
            stages.add(context.saveChangesAsync());
        }

        return CompletableFuture.allOf(stages.stream()
                .map(CompletionStage::toCompletableFuture)
                .toArray(CompletableFuture[]::new));
    }

    public CompletionStage<Void> rollbackAsync() {
        if (rolledBack) {
            return CompletableFuture.completedFuture(null);
        }

        rolledBack = true;
        List<CompletionStage<Void>> stages = new ArrayList<>();
        for (UnitOfWorkContext context : contexts.values()) {
            stages.add(context.rollbackAsync());
        }

        return CompletableFuture.allOf(stages.stream()
                .map(CompletionStage::toCompletableFuture)
                .toArray(CompletableFuture[]::new));
    }

    public CompletionStage<Void> completeAsync() {
        if (rolledBack) {
            return CompletableFuture.completedFuture(null);
        }

        if (completed || completing) {
            throw new IllegalStateException("Completion has already been requested for this unit of work.");
        }

        completing = true;
        CompletableFuture<Void> result = new CompletableFuture<>();
        saveChangesAsync().whenComplete((ignored, throwable) -> {
            try {
                if (throwable != null) {
                    failure = unwrap(throwable);
                    result.completeExceptionally(failure);
                    return;
                }

                completed = true;
                invokeCompletedHandlers().whenComplete((unused, handlerFailure) -> {
                    if (handlerFailure != null) {
                        failure = unwrap(handlerFailure);
                        result.completeExceptionally(failure);
                        return;
                    }

                    notifyCompleted();
                    result.complete(null);
                });
            } finally {
                completing = false;
            }
        });

        return result;
    }

    public void setOuter(UnitOfWork outer) {
        this.outer = outer;
    }

    public UnitOfWorkContext findContext(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key");
        }

        return contexts.get(key);
    }

    public void addContext(String key, UnitOfWorkContext context) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key");
        }

        if (context == null) {
            throw new IllegalArgumentException("context");
        }

        UnitOfWorkContext previous = contexts.putIfAbsent(key, context);
        if (previous != null) {
            throw new IllegalStateException("This unit of work already contains a context with the key: " + key);
        }
    }

    public UnitOfWorkContext getOrAddContext(String key, Supplier<UnitOfWorkContext> factory) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key");
        }

        if (factory == null) {
            throw new IllegalArgumentException("factory");
        }

        return contexts.computeIfAbsent(key, ignored -> factory.get());
    }

    @Override
    public void close() {
        if (disposed) {
            return;
        }

        disposed = true;

        for (UnitOfWorkContext context : contexts.values()) {
            context.close();
        }

        if (!completed || failure != null) {
            notifyFailed();
        }

        notifyDisposed();
    }

    protected void notifyCompleted(UnitOfWorkEvent event) {
        for (Consumer<UnitOfWorkEvent> listener : completedListeners) {
            listener.accept(event);
        }
    }

    protected void notifyFailed(UnitOfWorkFailure event) {
        for (Consumer<UnitOfWorkFailure> listener : failedListeners) {
            listener.accept(event);
        }
    }

    protected void notifyDisposed(UnitOfWorkEvent event) {
        for (Consumer<UnitOfWorkEvent> listener : disposedListeners) {
            listener.accept(event);
        }
    }

    private CompletionStage<Void> invokeCompletedHandlers() {
        CompletableFuture<Void> chain = CompletableFuture.completedFuture(null);
        for (Supplier<CompletionStage<Void>> handler : completedHandlers) {
            chain = chain.thenCompose(ignored -> handler.get());
        }

        return chain;
    }

    private void notifyCompleted() {
        notifyCompleted(new UnitOfWorkEvent(this));
    }

    private void notifyFailed() {
        notifyFailed(new UnitOfWorkFailure(this, failure, rolledBack));
    }

    private void notifyDisposed() {
        notifyDisposed(new UnitOfWorkEvent(this));
    }

    private static Throwable unwrap(Throwable throwable) {
        if (throwable instanceof java.util.concurrent.CompletionException completionException
                && completionException.getCause() != null) {
            return completionException.getCause();
        }

        return throwable;
    }
}
