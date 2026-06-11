
# Pipeline Module

A lightweight, async middleware pipeline framework for Java, inspired by ASP.NET Core's pipeline pattern. Enables chainable request/response processing with behaviors, delegates, and pluggable dependency injection.

---

## Architecture

```
Request / Context
        │
        ▼
┌──────────────────────┐
│  Pipeline.use(…)     │  ← Behavior 1 (logging, auth, validation…)
├──────────────────────┤
│  Pipeline.use(…)     │  ← Behavior 2 (transformation, enrichment…)
├──────────────────────┤
│  Pipeline.use(…)     │  ← Behavior N
├──────────────────────┤
│  Accumulate / Handler│  ← Terminal handler (user logic)
└──────────────────────┘
        │
        ▼
  Response / Void
```

Each behavior is a **middleware** that receives the context and a `next` delegate. Behaviors can:
- Execute code **before** the next component
- Execute code **after** the next component (via the returned `CompletionStage`)
- Short-circuit the pipeline by **not calling** `next.invoke()`
- **Modify** the context before passing it downstream

---

## Core Concepts

### Pipeline (Fire-and-Forget)

| Interface / Class | Description |
|-------------------|-------------|
| `Pipeline` | Builder interface — chain behaviors via `.use()`, then `.build()` or `.runAsync()` |
| `PipelineBase` | Abstract implementation with component list, reverse-chain build, and `@PipelineBehaviors` support |
| `PipelineDelegate` | `FunctionalInterface` — `CompletionStage<Void> invoke(Object context)` |
| `PipelineBehavior` | Behavior contract — `CompletionStage<Void> handleAsync(Object, PipelineDelegate)` |

### RequestResponsePipeline (Typed Request/Response)

| Interface / Class | Description |
|-------------------|-------------|
| `RequestResponsePipeline<TRequest, TResponse>` | Builder for typed request/response pipelines |
| `RequestResponsePipelineBase<TRequest, TResponse>` | Abstract implementation |
| `RequestResponsePipelineDelegate<TRequest, TResponse>` | `CompletionStage<TResponse> invoke(TRequest)` |
| `RequestResponsePipelineBehavior<TRequest, TResponse>` | `CompletionStage<TResponse> handleAsync(TRequest, PipelineDelegate)` |
| `RequestPipelineDelegate<TRequest>` | Fire-and-forget variant: `CompletionStage<Void> invoke(TRequest)` |

### Infrastructure

| Interface / Class | Description |
|-------------------|-------------|
| `PipelineFactory` | Creates `Pipeline` or `RequestResponsePipeline` instances |
| `DefaultPipelineFactory` | Default factory backed by `ServiceProvider` |
| `DefaultPipelineProvider` | Default `Pipeline` implementation |
| `DefaultRequestResponsePipelineProvider<TRequest, TResponse>` | Default typed pipeline implementation |
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

### Step 2: Basic Pipeline

```java
import com.euonia.pipeline.*;
import com.euonia.reflection.SimpleServiceProvider;

// Create resolver and pipeline
var resolver = new SimpleServiceProvider();
        Pipeline pipeline = new DefaultPipelineProvider(resolver)
                .use((ctx, next) -> {
                    System.out.println("Before: " + ctx);
                    return next.invoke(ctx).thenRun(() -> System.out.println("After: " + ctx));
                });

// Run
pipeline.

        runAsync("Hello, Pipeline!")
    .

        toCompletableFuture()
    .

        join();
```

### Step 3: Custom Behavior Class

```java
public class LoggingBehavior implements PipelineBehavior {
    @Override
    public CompletionStage<Void> handleAsync(Object context, PipelineDelegate next) {
        long start = System.nanoTime();
        return next.invoke(context).thenRun(() -> {
            long elapsed = (System.nanoTime() - start) / 1_000_000;
            System.out.println("[" + context.getClass().getSimpleName() + "] completed in " + elapsed + "ms");
        });
    }
}

// Usage
Pipeline pipeline = new DefaultPipelineProvider(resolver)
    .use(LoggingBehavior.class)
    .use((ctx, next) -> {
        // business logic
        return next.invoke(ctx);
    });
```

### Step 4: Request/Response Pipeline

```java
DefaultRequestResponsePipelineProvider<Integer, Integer> pipeline =
    new DefaultRequestResponsePipelineProvider<>(resolver);

pipeline.use(PlusOneBehavior.class);

int result = pipeline.runAsync(2, request -> CompletableFuture.completedFuture(request * 2))
    .toCompletableFuture()
    .join();

// result == 5  (2 * 2 + 1)
```

**PlusOneBehavior:**
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

## Usage Examples

### Lambda Behaviors

```java
// Inline lambda (fire-and-forget)
pipeline.use((ctx, next) -> {
    System.out.println("Processing: " + ctx);
    return next.invoke(ctx);
});

// Inline lambda (request/response)
requestResponsePipeline.use((req, next) ->
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
    private final RequestResponsePipelineDelegate<String, String> next;

    public ReflectionBehavior(RequestResponsePipelineDelegate<String, String> next) {
        this.next = next;
    }

    public CompletionStage<String> handleAsync(String context, SuffixService suffixService) {
        return next.invoke(context).thenApply(value -> suffixService.apply(value));
    }
}

// Usage
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
pipeline.runAsync(new CreateOrderCommand())
    .toCompletableFuture()
    .join();
// ValidationBehavior and AuditBehavior execute before any explicitly registered behaviors
```

### Fluent Builder with Composite Pipeline

```java
Pipeline pipeline = resolver.create()  // via PipelineFactory
    .use(AuthenticationBehavior.class)
    .use(AuthorizationBehavior.class)
    .use(ValidationBehavior.class, 0)  // insert at specific index
    .use((ctx, next) -> next.invoke(ctx))
    .build();  // freezes the pipeline, clears component list

pipeline.invoke(context).toCompletableFuture().join();
```

### Resolver Dependency Parameters in `handle` / `handleAsync`

Behaviors written as plain classes (not implementing `PipelineBehavior`) are resolved via reflection. The first parameter is the **context**, and all subsequent parameters are **auto-injected** from the `ServiceProvider`:

```java
// Plain class — method name must be "handle" or "handleAsync"
// Return type must be CompletionStage
public class MyBehavior {
    private final PipelineDelegate next;

    public MyBehavior(PipelineDelegate next) {
        this.next = next;
    }

    // context + auto-injected services
    public CompletionStage<Void> handleAsync(MyContext ctx, LoggerService logger, MetricsService metrics) {
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
public class SpringLoggingBehavior {
    private final PipelineDelegate next;
    private final LoggerService logger;  // Spring bean

    public SpringLoggingBehavior(PipelineDelegate next, LoggerService logger) {
        this.next = next;
        this.logger = logger;
    }

    public CompletionStage<Void> handleAsync(Object ctx) {
        logger.info("Pipeline processing: " + ctx);
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

## API Reference

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

| Method | Description |
|--------|-------------|
| `use(component)` | Appends a pipeline component |
| `use(component, index)` | Inserts a component at the given position |
| `use(handler)` | Appends a lambda handler `(ctx, next) → CompletionStage<Void>` |
| `use(type, args)` | Appends a component resolved from the given class with constructor arguments |
| `useOf(contextType, ahead)` | Auto-discovers `@PipelineBehaviors` annotation on the context type |
| `build()` | Freezes the pipeline and returns the outermost delegate |
| `runAsync(context)` | Shorthand: calls `useOf` then `build().invoke(context)` |
| `runAsync(context, accumulate)` | Shorthand with terminal handler |

### `PipelineBehavior`

```java
@FunctionalInterface
public interface PipelineBehavior {
    CompletionStage<Void> handleAsync(Object context, PipelineDelegate next);
}
```

### `RequestResponsePipeline<TRequest, TResponse>`

Same fluent API as `Pipeline`, but typed with `TRequest` / `TResponse`:

```java
CompletionStage<TResponse> runAsync(TRequest context);
CompletionStage<TResponse> runAsync(TRequest context, Function<TRequest, CompletionStage<TResponse>> accumulate);
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
