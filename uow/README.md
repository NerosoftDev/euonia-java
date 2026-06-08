# Unit of Work Module

A lightweight, async unit-of-work abstraction for coordinating transactional resources (database, message broker, etc.) in a single atomic operation. Inspired by the .NET `Euonia.Uow` module.

---

## Architecture

```
UnitOfWorkManager
        │
        ├── begin(options, requiresNew)
        │       │
        │       ▼
        │   ┌─────────────────────┐
        │   │    UnitOfWork        │
        │   │  (implements        │
        │   │   AutoCloseable)    │
        │   └────────┬────────────┘
        │            │
        │            ├── contexts: Map<String, UnitOfWorkContext>
        │            │       ├── "db"     → JdbcTransactionContext
        │            │       ├── "mq"     → MessageQueueContext
        │            │       └── "cache"  → CacheContext
        │            │
        │            ├── listeners
        │            │       ├── completedListeners
        │            │       ├── failedListeners
        │            │       └── disposedListeners
        │            │
        │            └── handlers
        │                    └── completedHandlers (async pre-completion)
        │
        └── getCurrent() → UnitOfWorkAccessor (ThreadLocal)
```

## Core Concepts

| Class / Interface | Description |
|-------------------|-------------|
| `UnitOfWork` | Coordinates contexts, listeners, and lifecycle (save → complete → dispose) |
| `UnitOfWorkManager` | Entry point — creates units, manages ambient scope via `ThreadLocal` |
| `UnitOfWorkContext` | Interface for transactional resources (save, commit, rollback, close) |
| `ChildUnitOfWork` | Delegates to parent when nesting without `requiresNew` |
| `UnitOfWorkAccessor` | `ThreadLocal` holder for the current ambient unit of work |
| `UnitOfWorkOptions` | Transactional flag, isolation level, timeout |
| `UnitOfWorkEnabled` | Marker interface for automatic interception |
| `@UnitOfWork` | Annotation for declarative unit-of-work boundaries |
| `UnitOfWorkHelper` | Static utilities for introspecting annotations |

## Lifecycle

```
initialize(options)
        │
        ▼
   [add contexts & business logic]
        │
        ├── completeAsync()
        │       │
        │       ├── saveChangesAsync()  ← flush all contexts
        │       ├── invokeCompletedHandlers()
        │       └── notifyCompleted()   ← fire completed listeners
        │
        └── close()  (AutoCloseable / try-with-resources)
                │
                ├── close all contexts
                ├── notifyFailed() if !completed
                └── notifyDisposed()
```

## Quick Start

### Programmatic API

```java
UnitOfWorkManager manager = new UnitOfWorkManager();

try (UnitOfWork uow = manager.begin(new UnitOfWorkOptions(true), false)) {
    uow.addContext("db", new JdbcTransactionContext(connection));

    uow.addCompletedListener(event ->
        log.info("Unit of work {} completed", event.getUnitOfWork().getId()));

    uow.addFailedListener(event ->
        log.error("Unit of work failed", event.getException()));

    // ... business logic ...

    uow.completeAsync().toCompletableFuture().join();
}
```

### Annotation-driven (with AOP)

```java
import com.euonia.uow.annotation.UnitOfWork;

@UnitOfWork
public class OrderService implements UnitOfWorkEnabled {

    public void placeOrder(Order order) {
        // Automatically wrapped in a unit of work
    }

    @UnitOfWork(disabled = true)
    public List<Order> findOrders() {
        // Read-only — no unit of work
    }
}
```

### Custom Transactional Context

```java
public class JdbcTransactionContext implements UnitOfWorkContext {
    private final Connection connection;

    public JdbcTransactionContext(Connection connection) {
        this.connection = connection;
    }

    @Override
    public CompletionStage<Void> saveChangesAsync() {
        return CompletableFuture.runAsync(() -> {
            // Flush pending statements
        });
    }

    @Override
    public CompletionStage<Void> commitAsync() {
        return CompletableFuture.runAsync(() -> connection.commit());
    }

    @Override
    public CompletionStage<Void> rollbackAsync() {
        return CompletableFuture.runAsync(() -> connection.rollback());
    }

    @Override
    public void close() {
        try { connection.close(); } catch (SQLException ignored) { }
    }
}
```

## Events

| Event Class | When Fired |
|-------------|------------|
| `UnitOfWorkEvent` | On successful completion and on disposal |
| `UnitOfWorkFailure` | On failure (exception or explicit rollback) |

## Isolation Levels

| Level | JDBC Constant |
|-------|---------------|
| `UNSPECIFIED` | `TRANSACTION_NONE` |
| `READ_UNCOMMITTED` | `TRANSACTION_READ_UNCOMMITTED` |
| `READ_COMMITTED` | `TRANSACTION_READ_COMMITTED` |
| `REPEATABLE_READ` | `TRANSACTION_REPEATABLE_READ` |
| `SERIALIZABLE` | `TRANSACTION_SERIALIZABLE` |

## Maven

```xml
<dependency>
    <groupId>com.euonia</groupId>
    <artifactId>unit-of-work</artifactId>
    <version>${euonia.version}</version>
</dependency>
```
