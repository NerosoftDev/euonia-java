# Unit of Work 模块

轻量级异步工作单元抽象，用于在单次原子操作中协调事务资源（数据库、消息代理等）。灵感来自 .NET `Euonia.Uow` 模块。

---

## 架构

```
UnitOfWorkManager
        │
        ├── begin(options, requiresNew)
        │       │
        │       ▼
        │   ┌─────────────────────┐
        │   │    UnitOfWork        │
        │   │  (实现              │
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
        │                    └── completedHandlers（异步前置完成回调）
        │
        └── getCurrent() → UnitOfWorkAccessor (ThreadLocal)
```

## 核心概念

| 类 / 接口 | 说明 |
|-----------|------|
| `UnitOfWork` | 协调上下文、监听器和生命周期（保存 → 完成 → 释放） |
| `UnitOfWorkManager` | 入口 — 创建工作单元，通过 `ThreadLocal` 管理环境作用域 |
| `UnitOfWorkContext` | 事务资源接口（保存、提交、回滚、关闭） |
| `ChildUnitOfWork` | 嵌套时无需 `requiresNew`，代理到父级 |
| `UnitOfWorkAccessor` | `ThreadLocal` 持有当前环境工作单元 |
| `UnitOfWorkOptions` | 事务标志、隔离级别、超时 |
| `UnitOfWorkEnabled` | 标记接口，用于自动拦截 |
| `@UnitOfWork` | 注解，用于声明式工作单元边界 |
| `UnitOfWorkHelper` | 静态工具类，用于检查注解 |

## 生命周期

```
initialize(options)
        │
        ▼
   [添加上下文 & 业务逻辑]
        │
        ├── completeAsync()
        │       │
        │       ├── saveChangesAsync()  ← 刷新所有上下文
        │       ├── invokeCompletedHandlers()
        │       └── notifyCompleted()   ← 触发完成监听器
        │
        └── close()  (AutoCloseable / try-with-resources)
                │
                ├── 关闭所有上下文
                ├── notifyFailed() 如果未完成
                └── notifyDisposed()
```

## 快速开始

### 编程式 API

```java
UnitOfWorkManager manager = new UnitOfWorkManager();

try (UnitOfWork uow = manager.begin(new UnitOfWorkOptions(true), false)) {
    uow.addContext("db", new JdbcTransactionContext(connection));

    uow.addCompletedListener(event ->
        log.info("工作单元 {} 已完成", event.getUnitOfWork().getId()));

    uow.addFailedListener(event ->
        log.error("工作单元执行失败", event.getException()));

    // ... 业务逻辑 ...

    uow.completeAsync().toCompletableFuture().join();
}
```

### 注解驱动（配合 AOP）

```java
import com.euonia.uow.annotation.UnitOfWork;

@UnitOfWork
public class OrderService implements UnitOfWorkEnabled {

    public void placeOrder(Order order) {
        // 自动包装在工作单元中
    }

    @UnitOfWork(disabled = true)
    public List<Order> findOrders() {
        // 只读 — 无需工作单元
    }
}
```

### 自定义事务上下文

```java
public class JdbcTransactionContext implements UnitOfWorkContext {
    private final Connection connection;

    public JdbcTransactionContext(Connection connection) {
        this.connection = connection;
    }

    @Override
    public CompletionStage<Void> saveChangesAsync() {
        return CompletableFuture.runAsync(() -> {
            // 刷新待执行语句
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

## 事件

| 事件类 | 触发时机 |
|--------|----------|
| `UnitOfWorkEvent` | 成功完成时和释放时 |
| `UnitOfWorkFailure` | 失败时（异常或显式回滚） |

## 隔离级别

| 级别 | JDBC 常量 |
|------|----------|
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
