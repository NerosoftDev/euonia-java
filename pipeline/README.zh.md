# Pipeline 模块

一个轻量级的异步中间件管道框架，受 ASP.NET Core 管道模式启发。支持可链式组合的请求/响应处理，包含行为（Behaviors）、委托（Delegates）和可插拔的依赖注入。

---

## 架构

```
请求 / 上下文
        │
        ▼
┌──────────────────────┐
│  Pipeline.use(…)     │  ← 行为 1（日志、认证、验证…）
├──────────────────────┤
│  Pipeline.use(…)     │  ← 行为 2（转换、增强…）
├──────────────────────┤
│  Pipeline.use(…)     │  ← 行为 N
├──────────────────────┤
│  Accumulate / Handler│  ← 终端处理器（用户逻辑）
└──────────────────────┘
        │
        ▼
  响应 / Void
```

每个行为是一个**中间件**，接收上下文和一个 `next` 委托。行为可以：
- 在下一个组件**之前**执行代码
- 在下一个组件**之后**执行代码（通过返回的 `CompletionStage`）
- **不调用** `next.invoke()` 来**短路**管道
- 在传入下游之前**修改**上下文

---

## 核心概念

### Pipeline（即发即忘）

| 接口 / 类 | 描述 |
|-----------|------|
| `Pipeline` | 构建器接口 — 通过 `.use()` 链式组合行为，然后调用 `.build()` 或 `.runAsync()` |
| `PipelineBase` | 抽象实现，包含组件列表、反向链构建和 `@PipelineBehaviors` 支持 |
| `PipelineDelegate` | `FunctionalInterface` — `CompletionStage<Void> invoke(Object context)` |
| `PipelineBehavior` | 行为契约 — `CompletionStage<Void> handleAsync(Object, PipelineDelegate)` |

### RequestResponsePipeline（类型化的请求/响应）

| 接口 / 类 | 描述 |
|-----------|------|
| `RequestResponsePipeline<TRequest, TResponse>` | 类型化请求/响应管道的构建器 |
| `RequestResponsePipelineBase<TRequest, TResponse>` | 抽象实现 |
| `RequestResponsePipelineDelegate<TRequest, TResponse>` | `CompletionStage<TResponse> invoke(TRequest)` |
| `RequestResponsePipelineBehavior<TRequest, TResponse>` | `CompletionStage<TResponse> handleAsync(TRequest, PipelineDelegate)` |
| `RequestPipelineDelegate<TRequest>` | 即发即忘变体：`CompletionStage<Void> invoke(TRequest)` |

### 基础设施

| 接口 / 类 | 描述 |
|-----------|------|
| `PipelineFactory` | 创建 `Pipeline` 或 `RequestResponsePipeline` 实例 |
| `DefaultPipelineFactory` | 基于 `ServiceProvider` 的默认工厂 |
| `DefaultPipelineProvider` | 默认的 `Pipeline` 实现 |
| `DefaultRequestResponsePipelineProvider<TRequest, TResponse>` | 默认的类型化管道实现 |
| `@PipelineBehaviors` | 从上下文类型自动发现行为的注解 |
| `ServiceProvider` | DI 抽象 — 独立使用（`SimpleServiceProvider`）或 Spring 集成 |

---

## 快速入门

### 第一步：添加依赖

```xml
<dependency>
    <groupId>com.euonia</groupId>
    <artifactId>pipeline</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 第二步：基础管道

```java
import com.euonia.pipeline.*;
import com.euonia.reflection.SimpleServiceProvider;

// 创建解析器和管道
var resolver = new SimpleServiceProvider();
        Pipeline pipeline = new DefaultPipelineProvider(resolver)
                .use((ctx, next) -> {
                    System.out.println("Before: " + ctx);
                    return next.invoke(ctx).thenRun(() -> System.out.println("After: " + ctx));
                });

// 运行
pipeline.

        runAsync("Hello, Pipeline!")
    .

        toCompletableFuture()
    .

        join();
```

### 第三步：自定义行为类

```java
public class LoggingBehavior implements PipelineBehavior {
    @Override
    public CompletionStage<Void> handleAsync(Object context, PipelineDelegate next) {
        long start = System.nanoTime();
        return next.invoke(context).thenRun(() -> {
            long elapsed = (System.nanoTime() - start) / 1_000_000;
            System.out.println("[" + context.getClass().getSimpleName() + "] 耗时 " + elapsed + "ms");
        });
    }
}

// 使用
Pipeline pipeline = new DefaultPipelineProvider(resolver)
    .use(LoggingBehavior.class)
    .use((ctx, next) -> {
        // 业务逻辑
        return next.invoke(ctx);
    });
```

### 第四步：请求/响应管道

```java
DefaultRequestResponsePipelineProvider<Integer, Integer> pipeline =
    new DefaultRequestResponsePipelineProvider<>(resolver);

pipeline.use(PlusOneBehavior.class);

int result = pipeline.runAsync(2, request -> CompletableFuture.completedFuture(request * 2))
    .toCompletableFuture()
    .join();

// result == 5  (2 * 2 + 1)
```

**PlusOneBehavior：**
```java
public class PlusOneBehavior implements RequestResponsePipelineBehavior<Integer, Integer> {
    @Override
    public CompletionStage<Integer> handleAsync(Integer context,
                                                 RequestResponsePipelineDelegate<Integer, Integer> next) {
        return next.invoke(context).thenApply(value -> value + 1);
    }
}
```

---

## 使用示例

### Lambda 行为

```java
// 内联 lambda（即发即忘）
pipeline.use((ctx, next) -> {
    System.out.println("Processing: " + ctx);
    return next.invoke(ctx);
});

// 内联 lambda（请求/响应）
requestResponsePipeline.use((req, next) ->
    next.invoke(req).thenApply(resp -> "Wrapped: " + resp)
);
```

### 通过 ServiceProvider 进行依赖注入

行为可以声明除上下文以外的额外参数 — 它们会从 `ServiceProvider` 自动解析。

```java
// 定义服务
public class SuffixService {
    private final String suffix;
    public SuffixService(String suffix) { this.suffix = suffix; }
    public String apply(String value) { return value + suffix; }
}

// 注册
resolver.register(SuffixService.class, new SuffixService("-ok"));

// 具有自动解析依赖的管道行为
public class ReflectionBehavior {
    private final RequestResponsePipelineDelegate<String, String> next;

    public ReflectionBehavior(RequestResponsePipelineDelegate<String, String> next) {
        this.next = next;
    }

    public CompletionStage<String> handleAsync(String context, SuffixService suffixService) {
        return next.invoke(context).thenApply(value -> suffixService.apply(value));
    }
}

// 使用
pipeline.use(ReflectionBehavior.class);
String result = pipeline.runAsync("input", CompletableFuture::completedFuture)
    .toCompletableFuture()
    .join();
// result == "input-ok"
```

### `@PipelineBehaviors` — 自动发现

在上下文类上添加注解，自动附加相关的行为：

```java
@PipelineBehaviors({ValidationBehavior.class, AuditBehavior.class})
public class CreateOrderCommand {
    // ...
}

// 调用 runAsync 时，注解会被自动发现：
pipeline.runAsync(new CreateOrderCommand())
    .toCompletableFuture()
    .join();
// ValidationBehavior 和 AuditBehavior 会在所有手动注册的行为之前执行
```

### Fluent Builder 与复合管道

```java
Pipeline pipeline = resolver.create()  // 通过 PipelineFactory
    .use(AuthenticationBehavior.class)
    .use(AuthorizationBehavior.class)
    .use(ValidationBehavior.class, 0)  // 插入到指定索引位置
    .use((ctx, next) -> next.invoke(ctx))
    .build();  // 冻结管道，清空组件列表

pipeline.invoke(context).toCompletableFuture().join();
```

### 在 `handle` / `handleAsync` 中解析依赖参数

使用普通类（不实现 `PipelineBehavior`）编写的行为通过反射解析。第一个参数是**上下文**，后续参数从 `ServiceProvider` **自动注入**：

```java
// 普通类 — 方法名必须为 "handle" 或 "handleAsync"
// 返回类型必须为 CompletionStage
public class MyBehavior {
    private final PipelineDelegate next;

    public MyBehavior(PipelineDelegate next) {
        this.next = next;
    }

    // 上下文 + 自动注入的服务
    public CompletionStage<Void> handleAsync(MyContext ctx, LoggerService logger, MetricsService metrics) {
        logger.info("Processing " + ctx);
        metrics.increment();
        return next.invoke(ctx);
    }
}
```

---

## Spring Boot 集成

### 配置

```java
@Configuration
public class PipelineConfiguration {
    @Bean
    public PipelineFactory pipelineFactory(ServiceProvider resolver) {
        return new DefaultPipelineFactory(resolver);
    }
}
```

### 在行为中使用 Spring 管理的 Bean

行为可以通过构造函数参数注入任何 Spring Bean。`ApplicationContextServiceProvider`（来自 `euonia-spring` 模块）会自动处理自动装配。

```java
@Component
public class SpringLoggingBehavior {
    private final PipelineDelegate next;
    private final LoggerService logger;  // Spring Bean

    public SpringLoggingBehavior(PipelineDelegate next, LoggerService logger) {
        this.next = next;
        this.logger = logger;
    }

    public CompletionStage<Void> handleAsync(Object ctx) {
        logger.info("管道处理: " + ctx);
        return next.invoke(ctx);
    }
}
```

```java
@Autowired
private PipelineFactory pipelineFactory;

public void execute() {
    Pipeline pipeline = pipelineFactory.create()
        .use(SpringLoggingBehavior.class)
        .use(TransactionalBehavior.class);

    pipeline.runAsync(new MyCommand()).toCompletableFuture().join();
}
```

---

## API 参考

### `Pipeline`

```java
public interface Pipeline {
    Pipeline use(Function<PipelineDelegate, PipelineDelegate> component);
    Pipeline use(Function<PipelineDelegate, PipelineDelegate> component, int index);
    Pipeline use(BiFunction<Object, PipelineDelegate, CompletionStage<Void>> handler);
    Pipeline use(Class<?> type, Object... args);
    Pipeline useOf(Class<?> contextType, boolean useAheadOfOthers);
    PipelineDelegate build();
    CompletionStage<Void> runAsync(Object context);
    CompletionStage<Void> runAsync(Object context, Function<Object, CompletionStage<Void>> accumulate);
}
```

| 方法 | 描述 |
|------|------|
| `use(component)` | 追加一个管道组件 |
| `use(component, index)` | 在指定位置插入一个组件 |
| `use(handler)` | 追加一个 lambda 处理器 `(ctx, next) → CompletionStage<Void>` |
| `use(type, args)` | 从指定类解析并追加一个组件，附带构造函数参数 |
| `useOf(contextType, ahead)` | 自动发现上下文类型上的 `@PipelineBehaviors` 注解 |
| `build()` | 冻结管道并返回最外层的委托 |
| `runAsync(context)` | 快捷方式：调用 `useOf` 后执行 `build().invoke(context)` |
| `runAsync(context, accumulate)` | 带有终端处理器的快捷方式 |

### `PipelineBehavior`

```java
@FunctionalInterface
public interface PipelineBehavior {
    CompletionStage<Void> handleAsync(Object context, PipelineDelegate next);
}
```

### `RequestResponsePipeline<TRequest, TResponse>`

与 `Pipeline` 相同的 Fluent API，但带有 `TRequest` / `TResponse` 类型参数：

```java
CompletionStage<TResponse> runAsync(TRequest context);
CompletionStage<TResponse> runAsync(TRequest context, Function<TRequest, CompletionStage<TResponse>> accumulate);
```

---

## 设计与实现细节

### 反向链式构建

调用 `.build()` 时，组件按**从内到外**的方式组装 — 最后注册的组件包裹前面的组件。这意味着：

```java
pipeline.use(A).use(B).use(C);
// 执行顺序: A → B → C
// 构造顺序: C 包裹 B, B 包裹 A
```

### 行为解析优先级

1. **`PipelineBehavior` 接口** — 如果类实现了 `PipelineBehavior`，则通过 `ServiceProvider.getServiceOrCreate()` 解析，并通过接口契约调用。
2. **基于反射** — 否则，框架搜索 `handle` 或 `handleAsync` 方法（返回 `CompletionStage`）。构造函数参数通过将 `next` 委托前置填充。

### 注解驱动的自动发现

`@PipelineBehaviors` 注解实现了**声明式管道配置**：

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PipelineBehaviors {
    Class<?>[] value();
}
```

当调用 `runAsync(context)`（或显式调用 `useOf(contextType, true)`）时，会扫描上下文类上的注解。注解中列出的行为会被注册到**所有手动注册组件之前**。

---

## 测试

管道模块设计为易于测试：

```java
// 使用 SimpleServiceProvider 进行单元测试
var resolver = new SimpleServiceProvider();
var pipeline = new DefaultPipelineProvider(resolver);

var results = new ArrayList<String>();
pipeline.use((ctx, next) -> {
    results.add("before");
    return next.invoke(ctx).thenRun(() -> results.add("after"));
});
pipeline.use((ctx, next) -> {
    results.add("handle");
    return next.invoke(ctx);
});

pipeline.runAsync("test").toCompletableFuture().join();
assertEquals(List.of("before", "handle", "after"), results);
```
