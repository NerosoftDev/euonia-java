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

/**
 * Coordinates transactional resources within a single atomic unit.
 *
 * <h3>Lifecycle</h3>
 * <ol>
 *   <li>{@link #initialize(UnitOfWorkOptions)} — called by the manager</li>
 *   <li>Register contexts via {@link #addContext} or {@link #getOrAddContext}</li>
 *   <li>{@link #completeAsync()} — save changes and commit</li>
 *   <li>{@link #close()} — release resources (implements {@link AutoCloseable})</li>
 * </ol>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * try (UnitOfWork uow = new UnitOfWork()) {
 *     uow.initialize(new UnitOfWorkOptions(true));
 *     uow.addContext("db", new JdbcTransactionContext(connection));
 *     // ... business logic ...
 *     uow.completeAsync().toCompletableFuture().join();
 * } // automatically disposes
 * }</pre>
 *
 * <h3>Listeners</h3>
 * <p>Register callbacks for success, failure, and disposal:
 * <ul>
 *   <li>{@link #addCompletedListener(Consumer)}</li>
 *   <li>{@link #addFailedListener(Consumer)}</li>
 *   <li>{@link #addDisposedListener(Consumer)}</li>
 *   <li>{@link #onCompleted(Supplier)} — async handler before completion event</li>
 * </ul>
 *
 * @see UnitOfWorkManager
 * @see UnitOfWorkContext
 * @see ChildUnitOfWork
 */
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

    /** Creates a unit of work with default (non-transactional) options. */
    public UnitOfWork() {
        this(new UnitOfWorkOptions());
    }

    /**
     * Creates a unit of work with the given default options.
     *
     * @param defaultOptions the fallback options; if {@code null}, non-transactional defaults are used
     */
    public UnitOfWork(UnitOfWorkOptions defaultOptions) {
        this.defaultOptions = defaultOptions == null ? new UnitOfWorkOptions() : defaultOptions;
    }

    /** @return the unique identifier for this unit of work */
    public String getId() {
        return id;
    }

    /**
     * Returns a mutable map for storing arbitrary data scoped to this
     * unit of work.
     *
     * @return the items map
     */
    public Map<String, Object> getItems() {
        return items;
    }

    /**
     * Returns an unmodifiable view of the registered transactional contexts.
     *
     * @return the contexts map
     */
    public Map<String, UnitOfWorkContext> getContexts() {
        return Collections.unmodifiableMap(contexts);
    }

    /** @return the active options, or {@code null} before initialization */
    public UnitOfWorkOptions getOptions() {
        return options;
    }

    /** @return the outer (parent) unit of work, or {@code null} if this is the outermost */
    public UnitOfWork getOuter() {
        return outer;
    }

    /** @return whether this unit of work has been reserved */
    public boolean isReserved() {
        return reserved;
    }

    /** @return the reservation name, or {@code null} if not reserved */
    public String getReservationName() {
        return reservationName;
    }

    /** @return whether {@link #close()} has been called */
    public boolean isDisposed() {
        return disposed;
    }

    /** @return whether {@link #completeAsync()} completed successfully */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Returns the failure that caused this unit of work to fail,
     * or {@code null} if it succeeded.
     *
     * @return the failure, or {@code null}
     */
    public Throwable getFailure() {
        return failure;
    }

    /**
     * Registers a listener to be notified when the unit of work
     * completes successfully.
     *
     * @param listener the callback
     */
    public void addCompletedListener(Consumer<UnitOfWorkEvent> listener) {
        completedListeners.add(Objects.requireNonNull(listener, "listener"));
    }

    /**
     * Registers a listener to be notified when the unit of work fails.
     *
     * @param listener the callback
     */
    public void addFailedListener(Consumer<UnitOfWorkFailure> listener) {
        failedListeners.add(Objects.requireNonNull(listener, "listener"));
    }

    /**
     * Registers a listener to be notified when the unit of work is disposed.
     *
     * @param listener the callback
     */
    public void addDisposedListener(Consumer<UnitOfWorkEvent> listener) {
        disposedListeners.add(Objects.requireNonNull(listener, "listener"));
    }

    /**
     * Registers an async handler that runs before the completed event is fired.
     * Handlers are chained sequentially.
     *
     * @param handler the async handler
     */
    public void onCompleted(Supplier<CompletionStage<Void>> handler) {
        completedHandlers.add(Objects.requireNonNull(handler, "handler"));
    }

    /**
     * Initializes the unit of work with the given options (called once by the manager).
     * Merges the supplied options with this unit's defaults.
     *
     * @param options the options for this unit of work
     * @throws IllegalArgumentException if {@code options} is {@code null}
     * @throws IllegalStateException    if already initialized
     */
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

    /**
     * Reserves this unit of work for a specific purpose.
     *
     * @param reservationName a descriptive name
     */
    public void reserve(String reservationName) {
        if (reservationName == null || reservationName.isBlank()) {
            throw new IllegalArgumentException("reservationName");
        }

        this.reservationName = reservationName;
        this.reserved = true;
    }

    /**
     * Saves pending changes across all registered contexts without committing.
     *
     * @return a stage that completes when all contexts have saved
     */
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

    /**
     * Rolls back all registered contexts.
     *
     * @return a stage that completes when all contexts have rolled back
     */
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

    /**
     * Completes the unit of work: saves changes, fires completion handlers,
     * then notifies listeners. May only be called once.
     *
     * @return a stage that completes when the unit of work is done
     * @throws IllegalStateException if completion has already been requested
     */
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

    /**
     * Sets the outer (parent) unit of work for nesting support.
     *
     * @param outer the outer unit of work
     */
    public void setOuter(UnitOfWork outer) {
        this.outer = outer;
    }

    /**
     * Finds a registered context by key.
     *
     * @param key the context key
     * @return the context, or {@code null} if not found
     */
    public UnitOfWorkContext findContext(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key");
        }

        return contexts.get(key);
    }

    /**
     * Registers a context under the given key. Fails if a context is
     * already registered with that key.
     *
     * @param key     the context key
     * @param context the context to register
     * @throws IllegalStateException if a context with this key already exists
     */
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

    /**
     * Gets an existing context or creates a new one using the supplied factory.
     *
     * @param key     the context key
     * @param factory the factory to create the context if absent
     * @return the existing or newly created context
     */
    public UnitOfWorkContext getOrAddContext(String key, Supplier<UnitOfWorkContext> factory) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key");
        }

        if (factory == null) {
            throw new IllegalArgumentException("factory");
        }

        return contexts.computeIfAbsent(key, ignored -> factory.get());
    }

    /**
     * Disposes the unit of work. Closes all contexts, fires failure
     * listeners if the unit did not complete successfully, and fires
     * disposal listeners. Idempotent — subsequent calls are no-ops.
     */
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

    /**
     * Notifies all registered completed listeners with the given event.
     *
     * @param event the completion event
     */
    protected void notifyCompleted(UnitOfWorkEvent event) {
        for (Consumer<UnitOfWorkEvent> listener : completedListeners) {
            listener.accept(event);
        }
    }

    /**
     * Notifies all registered failure listeners with the given event.
     *
     * @param event the failure event
     */
    protected void notifyFailed(UnitOfWorkFailure event) {
        for (Consumer<UnitOfWorkFailure> listener : failedListeners) {
            listener.accept(event);
        }
    }

    /**
     * Notifies all registered disposal listeners with the given event.
     *
     * @param event the disposal event
     */
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
