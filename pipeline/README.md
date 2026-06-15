
# Pipeline Module

A lightweight, async middleware pipeline framework for Java, inspired by ASP.NET Core's pipeline pattern. Provides a unified `Pipeline<TRequest, TResponse>` builder with generic behaviors, delegates, and pluggable dependency injection.

---

## Architecture

```
  TRequest
        │
        ▼
┌──────────────────────┐
│  Pipeline.use(…)     │  ← Behavior 1 (logging, auth, validation…)
├──────────────────────┤
│  Pipeline.use(…)     │  ← Behavior 2 (transformation, enrichment…)
├──────────────────────┤
│  Pipeline.use(…)     │  ← Behavior N
├──────────────────────┤
│  Terminal Handler    │  ← PipelineDelegate<TRequest, TResponse>
└──────────────────────┘
        │
        ▼
  TResponse
```

Each behavior is a **middleware** that receives the request and a `next` delegate. Behaviors can:
- Execute code **before** the next component
- Execute code **after** the next component (via the returned `CompletionStage`)
- Short-circuit the pipeline by **not calling** `next.invoke()`
- **Transform** the response before returning upstream

For fire-and-forget scenarios, use `Pipeline<Object, Void>` — the pipeline returns `CompletionStage<Void>`.

---

## Core Concepts

| Interface / Class | Description |
|-------------------|-------------|
| `Pipeline<TRequest, TResponse>` | Builder interface — chain behaviors via `.use()`, then `.build()` or `.runAsync()` |
| `PipelineBase<TRequest, TResponse>` | Abstract implementation with component list, reverse-chain build, and `@PipelineBehaviors` support |
| `PipelineDelegate<TRequest, TResponse>` | `@FunctionalInterface` — `CompletionStage<TResponse> invoke(TRequest request)` |
| `PipelineBehavior<TRequest, TResponse>` | Behavior contract — `CompletionStage<TResponse> handleAsync(TRequest context, PipelineDelegate<TRequest, TResponse> next)` |
| `PipelineFactory` | Creates `Pipeline<TRequest, TResponse>` instances |
| `DefaultPipelineFactory` | Default factory backed by `ServiceProvider` |
| `DefaultPipelineProvider<TRequest, TResponse>` | Default `Pipeline` implementation with reflection-based behavior resolution |
| `@PipelineBehaviors` | Annotation to auto-discover behaviors from context type |
| `ServiceProvider` | Abstraction for DI — standalone (`SimpleServiceProvider`) or Spring integration |

---

## Getting Started

### Step 1: Add Dependency

```xml
<dependency>
    <groupId>com.euonia</groupId>
    <artifactId>pipeline</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Step 2: Fire-and-Forget Pipeline

```java
import com.euonia.pipeline.*;
import com.euonia.reflection.SimpleServiceProvider;

// Create resolver and pipeline
var resolver = new SimpleServiceProvider();
Pipeline<Object, Void> pipeline = new DefaultPipelineProvider<>(resolver)
        .use((ctx, next) -> {
            System.out.println("Before: " + ctx);
            return next.invoke(ctx).thenRun(() -> System.out.println("After: " + ctx));
        });

// Run
pipeline.runAsync("Hello, Pipeline!")
        .toCompletableFuture()
        .join();
```

### Step 3: Custom Behavior Class

```java
public class LoggingBehavior<TRequest, TResponse> implements PipelineBehavior<TRequest, TResponse> {
    @Override
    public CompletionStage<TResponse> handleAsync(TRequest context, PipelineDelegate<TRequest, TResponse> next) {
        long start = System.nanoTime();
        return next.invoke(context).thenRun(() -> {
            long elapsed = (System.nanoTime() - start) / 1_000_000;
            System.out.println("[" + context.getClass().getSimpleName() + "] completed in " + elapsed + "ms");
        });
    }
}

// Usage
Pipeline<Object, Void> pipeline = new DefaultPipelineProvider<>(resolver)
    .use(LoggingBehavior.class)
    .use((ctx, next) -> {
        // business logic
        return next.invoke(ctx);
    });
```

### Step 4: Typed Request/Response Pipeline

```java
Pipeline<Integer, Integer> pipeline = new DefaultPipelineProvider<>(resolver);

pipeline.use(PlusOneBehavior.class);

int result = pipeline.runAsync(2, request -> CompletableFuture.completedFuture(request * 2))
    .toCompletableFuture()
    .join();

// result == 5  (2 * 2 + 1)
```

**PlusOneBehavior:**
```java
public class PlusOneBehavior implements PipelineBehavior<Integer, Integer> {
    @Override
    public CompletionStage<Integer> handleAsync(Integer context,
                                                 PipelineDelegate<Integer, Integer> next) {
        return next.invoke(context).thenApply(value -> value + 1);
    }
}
```

---

## Usage Examples

### Lambda Behaviors

```java
// Inline lambda (fire-and-forget)
Pipeline<Object, Void> pipeline = new DefaultPipelineProvider<>(resolver);
pipeline.use((ctx, next) -> {
    System.out.println("Processing: " + ctx);
    return next.invoke(ctx);
});

// Inline lambda (typed request/response)
Pipeline<String, String> typedPipeline = new DefaultPipelineProvider<>(resolver);
typedPipeline.use((String req, PipelineDelegate<String, String> next) ->
    next.invoke(req).thenApply(resp -> "Wrapped: " + resp)
);
```

### Dependency Injection via ServiceProvider

Behaviors can declare extra parameters beyond the context — they are resolved automatically from the `ServiceProvider`.

```java
// Define a service
public class SuffixService {
    private final String suffix;
    public SuffixService(String suffix) { this.suffix = suffix; }
    public String apply(String value) { return value + suffix; }
}

// Register it
resolver.register(SuffixService.class, new SuffixService("-ok"));

// Pipeline behavior with auto-resolved dependency
public class ReflectionBehavior {
    private final PipelineDelegate<String, String> next;

    public ReflectionBehavior(PipelineDelegate<String, String> next) {
        this.next = next;
    }

    public CompletionStage<String> handleAsync(String context, SuffixService suffixService) {
        return next.invoke(context).thenApply(value -> suffixService.apply(value));
    }
}

// Usage
Pipeline<String, String> pipeline = new DefaultPipelineProvider<>(resolver);
pipeline.use(ReflectionBehavior.class);
String result = pipeline.runAsync("input", CompletableFuture::completedFuture)
    .toCompletableFuture()
    .join();
// result == "input-ok"
```

### `@PipelineBehaviors` — Auto-Discovery

Annotate your context class to automatically attach relevant behaviors:

```java
@PipelineBehaviors({ValidationBehavior.class, AuditBehavior.class})
public class CreateOrderCommand {
    // ...
}

// When you call runAsync, the annotation is discovered automatically:
Pipeline<CreateOrderCommand, Void> pipeline = new DefaultPipelineProvider<>(resolver);
pipeline.runAsync(new CreateOrderCommand())
    .toCompletableFuture()
    .join();
// ValidationBehavior and AuditBehavior execute before any explicitly registered behaviors
```

### Fluent Builder with Composite Pipeline

```java
Pipeline<Object, Void> pipeline = pipelineFactory.<Object, Void>create()
    .use(AuthenticationBehavior.class)
    .use(AuthorizationBehavior.class)
    .use(ValidationBehavior.class, 0)  // insert at specific index
    .use((ctx, next) -> next.invoke(ctx))
    .build();  // freezes the pipeline, clears component list

pipeline.build().invoke(context).toCompletableFuture().join();
```

### Resolver Dependency Parameters in `handle` / `handleAsync`

Behaviors written as plain classes (not implementing `PipelineBehavior`) are resolved via reflection. The first parameter is the **context**, and all subsequent parameters are **auto-injected** from the `ServiceProvider`:

```java
// Plain class — method name must be "handle" or "handleAsync"
// Return type must be CompletionStage
public class MyBehavior<TRequest, TResponse> {
    private final PipelineDelegate<TRequest, TResponse> next;

    public MyBehavior(PipelineDelegate<TRequest, TResponse> next) {
        this.next = next;
    }

    // context + auto-injected services
    public CompletionStage<TResponse> handleAsync(TRequest ctx, LoggerService logger, MetricsService metrics) {
        logger.info("Processing " + ctx);
        metrics.increment();
        return next.invoke(ctx);
    }
}
```

---

## Spring Boot Integration

### Configuration

```java
@Configuration
public class PipelineConfiguration {
    @Bean
    public PipelineFactory pipelineFactory(ServiceProvider resolver) {
        return new DefaultPipelineFactory(resolver);
    }
}
```

### Using Spring-Managed Beans in Behaviors

Behaviors can inject any Spring bean through constructor parameters. The `ApplicationContextServiceProvider` (from `euonia-spring` module) handles auto-wiring automatically.

```java
@Component
public class SpringLoggingBehavior implements PipelineBehavior<Object, Void> {
    private final PipelineDelegate<Object, Void> next;
    private final LoggerService logger;  // Spring bean

    public SpringLoggingBehavior(PipelineDelegate<Object, Void> next, LoggerService logger) {
        this.next = next;
        this.logger = logger;
    }

    @Override
    public CompletionStage<Void> handleAsync(Object ctx, PipelineDelegate<Object, Void> next) {
        logger.info("Pipeline processing: " + ctx);
        return next.invoke(ctx);
    }
}
```

```java
@Autowired
private PipelineFactory pipelineFactory;

public void execute() {
    Pipeline<MyCommand, Void> pipeline = pipelineFactory.<MyCommand, Void>create()
        .use(SpringLoggingBehavior.class)
        .use(TransactionalBehavior.class);

    pipeline.runAsync(new MyCommand()).toCompletableFuture().join();
}
```

---

## API Reference

### `Pipeline<TRequest, TResponse>`

```java
public interface Pipeline<TRequest, TResponse> {
    Pipeline<TRequest, TResponse> use(Function<PipelineDelegate<TRequest, TResponse>, PipelineDelegate<TRequest, TResponse>> component);
    Pipeline<TRequest, TResponse> use(Function<PipelineDelegate<TRequest, TResponse>, PipelineDelegate<TRequest, TResponse>> component, int index);
    Pipeline<TRequest, TResponse> use(PipelineBehavior<TRequest, TResponse> behavior);
    Pipeline<TRequest, TResponse> use(BiFunction<TRequest, PipelineDelegate<TRequest, TResponse>, CompletionStage<TResponse>> handler);
    Pipeline<TRequest, TResponse> use(Class<?> type, Object... args);
    Pipeline<TRequest, TResponse> useOf(Class<?> contextType, boolean useAheadOfOthers);
    PipelineDelegate<TRequest, TResponse> build();
    CompletionStage<TResponse> runAsync(TRequest context);
    CompletionStage<TResponse> runAsync(TRequest context, Function<TRequest, CompletionStage<TResponse>> accumulate);
}
```

| Method | Description |
|--------|-------------|
| `use(component)` | Appends a pipeline component |
| `use(component, index)` | Inserts a component at the given position |
| `use(behavior)` | Appends a `PipelineBehavior` instance |
| `use(handler)` | Appends a lambda handler `(ctx, next) → CompletionStage<TResponse>` |
| `use(type, args)` | Appends a component resolved from the given class with constructor arguments |
| `useOf(contextType, ahead)` | Auto-discovers `@PipelineBehaviors` annotation on the context type |
| `build()` | Freezes the pipeline and returns the outermost delegate |
| `runAsync(context)` | Shorthand: calls `useOf` then `build().invoke(context)` |
| `runAsync(context, accumulate)` | Shorthand with terminal handler |

### `PipelineBehavior<TRequest, TResponse>`

```java
@FunctionalInterface
public interface PipelineBehavior<TRequest, TResponse> {
    CompletionStage<TResponse> handleAsync(TRequest context, PipelineDelegate<TRequest, TResponse> next);
}
```

### `PipelineDelegate<TRequest, TResponse>`

```java
@FunctionalInterface
public interface PipelineDelegate<TRequest, TResponse> {
    CompletionStage<TResponse> invoke(TRequest request);
}
```

### `PipelineFactory`

```java
public interface PipelineFactory {
    <TRequest, TResponse> Pipeline<TRequest, TResponse> create();
}
```

A single `create()` method returns a generic `Pipeline<TRequest, TResponse>` — usable for both fire-and-forget (`Pipeline<Object, Void>`) and typed request/response scenarios.

---

## Design & Implementation Details

### Reverse-Chain Construction

When `.build()` is called, components are assembled **inside-out** — the last registered component wraps the previous ones. This means:

```java
pipeline.use(A).use(B).use(C);
// Execution order: A → B → C
// Construction: C wraps B wraps A
```

### Behavior Resolution Priority

1. **`PipelineBehavior` interface** — if the class implements `PipelineBehavior`, it is resolved via `ServiceProvider.getServiceOrCreate()` and invoked through the interface contract.
2. **Reflection-based** — otherwise, the framework searches for `handle` or `handleAsync` methods (returning `CompletionStage`). Constructor arguments are populated by prepending the `next` delegate.

### Annotation-Driven Auto-Discovery

The `@PipelineBehaviors` annotation enables **declarative pipeline configuration**:

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PipelineBehaviors {
    Class<?>[] value();
}
```

When `runAsync(context)` is called (or `useOf(contextType, true)` explicitly), the annotation on the context's class is scanned. Behaviors listed in the annotation are registered **ahead of** all manually registered components.

---

## Testing

The pipeline module is designed for testability:

```java
// Unit test with SimpleServiceProvider
var resolver = new SimpleServiceProvider();
Pipeline<Object, Void> pipeline = new DefaultPipelineProvider<>(resolver);

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

---

## Design & Implementation Details

### Reverse-Chain Construction

When `.build()` is called, components are assembled **inside-out** — the last registered component wraps the previous ones. This means:

```java
pipeline.use(A).use(B).use(C);
// Execution order: A → B → C
// Construction: C wraps B wraps A
```

### Behavior Resolution Priority

1. **`PipelineBehavior` interface** — if the class implements `PipelineBehavior`, it is resolved via `ServiceProvider.getServiceOrCreate()` and invoked through the interface contract.
2. **Reflection-based** — otherwise, the framework searches for `handle` or `handleAsync` methods (returning `CompletionStage`). Constructor arguments are populated by prepending the `next` delegate.

### Annotation-Driven Auto-Discovery

The `@PipelineBehaviors` annotation enables **declarative pipeline configuration**:

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PipelineBehaviors {
    Class<?>[] value();
}
```

When `runAsync(context)` is called (or `useOf(contextType, true)` explicitly), the annotation on the context's class is scanned. Behaviors listed in the annotation are registered **ahead of** all manually registered components.

---

## Testing

The pipeline module is designed for testability:

```java
// Unit test with SimpleServiceProvider
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
