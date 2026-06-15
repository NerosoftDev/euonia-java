# Euonia (Java)

> *Eunoia* — from Greek *εὔνοια*: beautiful thinking, goodwill, a well-disposed mind.

Euonia is a development framework for building enterprise Java applications. It combines **Object-Oriented Scalable Business Architecture (OSBA)** with **Domain-Driven Design (DDD)** principles to provide a comprehensive foundation for creating robust, maintainable business applications. The framework is built on **Java 17+** and integrates seamlessly with **Spring Boot**.

Euonia is also available for **[.NET](https://github.com/NerosoftDev/Euonia)** — this repository hosts the **Java edition**.

---

## Modules

```mermaid
graph TD
    subgraph "Euonia Java"
        direction TB
        DDD --> Core
        DDD --> UoW
        DDD --> Pipeline
        OSBA --> Core
        UoW --> Core
        Pipeline --> Core
        Spring --> Core
        Spring --> UoW
        Spring --> OSBA
        Spring --> Pipeline
        BusAbstract --> Core
        BusCore --> BusAbstract
        BusCore --> Pipeline
        BusInmemory --> BusAbstract
        BusRabbitmq --> BusAbstract
        BusKafka --> BusAbstract
        Sample --> DDD
        Sample --> OSBA
        Sample --> Pipeline
        Sample --> Spring
    end

    style Core fill:#4A90D9,color:#fff
    style DDD fill:#50B86C,color:#fff
    style UoW fill:#1F6FEB,color:#fff
    style OSBA fill:#E8833A,color:#fff
    style Pipeline fill:#E74C3C,color:#fff
    style Spring fill:#2ECC71,color:#fff
    style BusAbstract fill:#F39C12,color:#fff
    style BusCore fill:#E67E22,color:#fff
    style BusInmemory fill:#D35400,color:#fff
    style BusRabbitmq fill:#C0392B,color:#fff
    style BusKafka fill:#8E44AD,color:#fff
    style Sample fill:#9B59B6,color:#fff
```

### Core (`euonia-core`)
> Foundation library: base classes, ID generation, reflection utilities, tuples, HTTP exceptions, security, and validation annotations.

| Package | Description |
|---------|-------------|
| `com.euonia.core` | Unified `ObjectId` (supports Snowflake, UUID, ULID, Random), `SnowflakeId`, `ULID`, `ShortUniqueId`, `Singleton<T>`, `PriorityQueue`, `Pair<L,R>` |
| `com.euonia.tuple` | Immutable typed tuples: `Solo`, `Duet`, `Trio`, `Quartet`, `Quintet`, `Sextet`, `Septet`, `Octet`, `Nonet`, `Decet` |
| `com.euonia.http` | HTTP status exceptions: `BadRequestException` (400), `UnauthorizedAccessException` (401), `ForbiddenException` (403), `ResourceNotFoundException` (404), `ConflictException` (409), and more |
| `com.euonia.security` | `UserPrincipal`, `UserClaimTypes`, `AuthenticationException`, `CredentialException`, `UnauthorizedAccessException` |
| `com.euonia.annotation` | `@Required`, `@Validator`, `@Validation` — metadata for field validation |
| `com.euonia.reflection` | `TypeHelper`, `GenericType<T>`, `@DisplayName` |

### DDD (`euonia-domain-driven-design`)
> Domain-Driven Design abstractions: entities, aggregates, value objects, domain events, application services, use cases, and auditing support.

| Package | Class | Purpose |
|---------|-------|---------|
| `com.euonia.domain` | `Entity<ID>` / `EntityBase<ID>` | Base interface and abstract class for domain entities with identity |
| `com.euonia.domain` | `Aggregate<ID>` / `AggregateBase<ID>` | Aggregate root with domain event management (`raiseEvent`, `clearEvents`, `attachEvents`) |
| `com.euonia.domain` | `HasDomainEvents` | Contract for aggregates that manage domain events and handlers |
| `com.euonia.domain` | `ValueObject<T>` | Immutable value object with reflection-based `equals`, `hashCode`, and `compareTo` |
| `com.euonia.domain.event` | `Event` / `EventBase` | Core event contract: id, sequence, intent, originator metadata |
| `com.euonia.domain.event` | `DomainEvent` / `DomainEventBase` | Domain event with aggregate attachment and `EventAggregate` projection |
| `com.euonia.domain.event` | `ApplicationEvent` / `ApplicationEventBase` | Application-level (integration) event base classes |
| `com.euonia.domain.event` | `EventAggregate` | Aggregate-shaped event data: id, eventId, typeName, originator, timestamp, sequence |
| `com.euonia.domain.auditing` | `@Audited` / `AuditRecord` / `AuditStore` | Change auditing support for domain entities |
| `com.euonia.application` | `ApplicationService` / `BaseApplicationService` | Application service marker and base class with dependency resolution |
| `com.euonia.usecase` | `UseCase<I,O>` / `UseCasePresenter` | Input/output use-case contract with reactive result publishing |
| `com.euonia.usecase` | `UseCaseSuccess` / `UseCaseFailure` | Output ports for success and failure handling |

### UoW (`euonia-unit-of-work`)
> Unit of Work abstraction for transaction boundaries, commit/rollback lifecycle, and consistent persistence orchestration.

| Class / Interface | Purpose |
|-------------------|---------|
| `IUnitOfWork` | Unit-of-work contract with lifecycle methods (`saveChanges`, `commit`, `rollback`) |
| `IUnitOfWorkManager` | Creates/manages current unit-of-work scope |
| `UnitOfWork` | Default unit-of-work implementation |
| `UnitOfWorkBase` | Base class for shared transaction flow |
| `UnitOfWorkInterceptor` | Intercepts application flow to attach UoW boundaries |
| `IUnitOfWorkAccessor` | Access current active unit-of-work context |

### Pipeline (`euonia-pipeline`)
> Middleware pipeline framework inspired by ASP.NET Core pipeline pattern — unified `Pipeline<TRequest, TResponse>` with chainable behaviors, delegates, and dependency injection integration.

| Interface / Class | Description |
|-------------------|-------------|
| `Pipeline<TRequest, TResponse>` | Pipeline builder: chain components via `use()`, build delegate, run async |
| `PipelineBase<TRequest, TResponse>` | Abstract base with component registration, reverse-chain build, and `@PipelineBehaviors` annotation support |
| `PipelineDelegate<TRequest, TResponse>` | `@FunctionalInterface`: `CompletionStage<TResponse> invoke(TRequest request)` |
| `PipelineBehavior<TRequest, TResponse>` | Behavior interface: `CompletionStage<TResponse> handleAsync(TRequest, PipelineDelegate<TRequest, TResponse>)` |
| `PipelineFactory` / `DefaultPipelineFactory` | Factory for creating `Pipeline<TRequest, TResponse>` instances |
| `DefaultPipelineProvider<TRequest, TResponse>` | Default implementation resolving behaviors via `ServiceProvider` (reflection or DI) |
| `@PipelineBehaviors` | Annotation to auto-attach behaviors by context type |

**Key features:**
- Fluent API: chain behaviors via `.use()` with lambda, class, or `@PipelineBehaviors` discovery
- Single `Pipeline<TRequest, TResponse>` for both fire-and-forget (`Pipeline<Object, Void>`) and typed request/response scenarios
- Delegate-based composition with reverse-chain construction (innermost executes first)
- `ServiceProvider` abstraction enables both standalone and Spring-integrated usage
- Async throughout via `CompletionStage`

```java
// Create a pipeline
Pipeline<Object, Void> pipeline = new DefaultPipelineProvider<>(resolver)
    .use((ctx, next) -> next.invoke(ctx).thenRun(() -> System.out.println("Log: done")))
    .use(LoggingBehavior.class);

// Run
pipeline.runAsync(new MyContext()).toCompletableFuture().join();
```

### Bus Abstract (`euonia-bus-abstract`)
> Foundational messaging abstractions: message contracts, conventions, transport strategies, metadata, and marker annotations for the bus layer. Depends on `core`.

**Core Contracts**

| Class / Interface | Purpose |
|-------------------|---------|
| `MessageContext` | Runtime message context: reply, failure, and completion event publishers |
| `MessageContextBase` | Thread-safe context implementation with event publishing and close-time completion |
| `HandlerContext` | Handler-level context contract for subscription and dispatch |
| `RoutedMessage` | Abstract message envelope: payload, IDs, correlation ID, channel, metadata, headers |
| `MessageEnvelope` | Minimal routed envelope contract (messageId, correlationId, conversationId, channel) |
| `MessageMetadata` | Typed metadata map implementing `Map<String,Object>` with `get(key, type)` accessor |
| `MessageHeaders` | Header name constants: `MESSAGE_ID`, `CORRELATION_ID`, `CONVERSATION_ID`, `CONTENT_TYPE`, `REQUEST_TRACE_ID`, `AUTHORIZATION` |
| `MessageBusOptions` | Bus configuration: default transport, pipeline behavior toggle, convention & strategy access |
| `Dispatcher` | Dispatch contract: `List<String> determine(Class<?>)` |
| `MessageRegistration` | Immutable record: channel, messageType, handlerType, method |
| `MessageConventionType` | Enum: `NONE`, `UNICAST`, `MULTICAST`, `REQUEST` |

**Conventions**

| Class / Interface | Purpose |
|-------------------|---------|
| `MessageConvention` | Contract: `isUnicastType`, `isMulticastType`, `isRequestType` |
| `DefaultMessageConvention` | Class-hierarchy convention using `Unicast`/`Multicast`/`Request` contract interfaces |
| `AnnotationMessageConvention` | Annotation-based convention using `@Unicast`/`@Multicast`/`@Request` annotations |
| `BaseMessageConvention` | Composite convention with caches, pluggable conventions, and per-kind overrides |
| `OverridableMessageConvention` | Delegating convention with settable predicate overrides for each type |
| `MessageConventionBuilder` | Fluent builder: `evaluateUnicast`, `evaluateMulticast`, `evaluateRequest`, `add(C)` |

**Transport Strategies**

| Class / Interface | Purpose |
|-------------------|---------|
| `TransportStrategy` | Contract: `outgoing(Class<?>)`, `incoming(Class<?>)` |
| `BaseTransportStrategy` | Composite strategy with caching and pluggable strategy list |
| `DefaultTransportStrategy` | No-op fallback (always returns `false`) |
| `AnnotationTransportStrategy` | Matches `@DispatchIn`/`@ReceiveIn` annotation transport names |
| `OverridableTransportStrategy` | Delegate with settable predicate overrides |
| `LocalMessageTransportStrategy` | Matches `@LocalMessage`-annotated types |
| `DistributedMessageTransportStrategy` | Matches `@DistributedMessage`-annotated types |

**Annotations**

| Annotation | Target | Purpose |
|------------|--------|---------|
| `@Subscribe` | Method | Declares a message handler method; `value` = channel, `group` = consumer group |
| `@Command` | Type | Marks a type as a unicast command |
| `@Event` | Type | Marks a type as a multicast event |
| `@Request` | Type | Marks a request type with explicit `responseType` |
| `@Channel` | Type | Overrides the default channel name (FQCN) |
| `@Enqueue` | Type | Queue mapping with `value` (queue name) and `priority` |
| `@LocalMessage` | Type | Marks a type for local transport only |
| `@DistributedMessage` | Type | Marks a type for distributed transport only |
| `@DispatchIn` | Type | Constrains outgoing dispatch to specified transports |
| `@ReceiveIn` | Type | Constrains incoming receive to specified transports |

**Contracts**

| Interface | Purpose |
|-----------|---------|
| `Queue` | Marker: unicast point-to-point message |
| `Topic` | Marker: publish-subscribe message |
| `Request<R>` | Marker: request-response message with response type `R` |
| `Transport` | Transport abstraction: `publishAsync`, `sendAsync`, `requestAsync` |

**Recipients**

| Interface | Purpose |
|-----------|---------|
| `Recipient` | Base contract: `getName()`; extends `AutoCloseable` |
| `Executor` | Marker sub-interface of `Recipient` |
| `Subscriber` | Marker sub-interface of `Recipient` |

**Events**

| Class | Purpose |
|-------|---------|
| `MessageSubscribedEvent` | Emitted when a handler is subscribed (channel, messageType, handlerType) |
| `MessageReceivedEvent` | Emitted when a message is received by transport |
| `MessageAcknowledgedEvent` | Emitted when a message is acknowledged (RECEIVED type) |
| `MessageDeliveredEvent` | Emitted when a message is successfully delivered |
| `MessageHandledEvent` | Emitted when a handler completes processing (message + handler type) |
| `MessageRepliedEvent` | Emitted with response result |
| `MessageProcessedEvent` | Base event: message + context + `MessageProcessType` |
| `MessageProcessType` | Enum: `SEND`, `DELIVERED`, `RECEIVED` |

**Exceptions**

| Class | Purpose |
|-------|---------|
| `MessageTypeException` | Invalid or unsupported message type for routing |
| `MessageProcessingException` | Handler or processing failure |
| `MessageDeliverException` | Message delivery failure |
| `MessageTransportException` | Transport-layer failure |

### Bus Core (`euonia-bus-core`)
> Runtime orchestration layer: handler discovery, registration, dispatch, and bus API. Depends on `pipeline` and `bus-abstract`.

| Class / Interface | Purpose |
|-------------------|---------|
| `Bus` | Top-level bus interface for `send`, `publish`, `call` operations |
| `MessageBus` | Bus implementation shell |
| `Handler<M, R>` | Typed message handler interface |
| `StrategicDispatcher` | Dispatcher that resolves transport names via configured strategies |
| `MessageHandlerFinder` | Scans classes for `@Subscribe` methods and `Handler` implementations |
| `DefaultHandlerContext` | Runtime handler resolution and invocation via `ServiceProvider` |
| `MessageHandler` / `MessageHandlerFactory` | Handler wrapper and factory for per-channel dispatch |
| `PipelineMessage` | Wraps message execution through `Pipeline` |
| `MessageCache` | Centralized channel naming (defaults to FQCN, `@Channel` override) |
| `SendOptions` / `PublishOptions` / `CallOptions` | Typed operation options |
| `ExtendableOptions` | Base class for extensible option sets |

**Key features:**
- Discovers handlers via `@Subscribe` annotated methods or `Handler<M,R>` interface
- Single-handler channels support request/response (unicast); multi-handler channels run in parallel (multicast)
- `TransportStrategy` system maps message types to transports (local vs. distributed)
- Integrates with Pipeline for middleware-style message processing

### Bus InMemory (`euonia-bus-inmemory`)
> In-memory transport adapter (scaffold). Provides local message dispatch without external infrastructure.

### Bus RabbitMQ (`euonia-bus-rabbitmq`)
> RabbitMQ transport adapter (scaffold). Provides distributed message dispatch via RabbitMQ broker.

### Bus Kafka (`euonia-bus-kafka`)
> Kafka transport adapter (scaffold). Provides distributed message dispatch via Apache Kafka broker.

### Spring (`euonia-spring`)
> Spring Framework integration module. Bridges `ServiceProvider` with Spring's `ApplicationContext` for seamless dependency injection in pipeline and other Euonia components.

| Class | Description |
|-------|-------------|
| `ApplicationContextServiceProvider` | `ServiceProvider` implementation backed by Spring's `ApplicationContext` — supports `getBeanProvider`, `autowireBean`, and constructor-argument-based bean creation |
| `ServiceProviderConfiguration` | Spring `@Configuration` auto-wiring `ServiceProvider` as a bean |

**Key features:**
- Enables Spring DI for pipeline behaviors and other Euonia components
- Auto-wires Spring-managed beans into pipeline delegates
- Fallback to reflection-based construction with autowiring support
- Minimal setup: just `@Import(ServiceProviderConfiguration.class)` or component-scan

### OSBA (`euonia-osba`)
> **Object-Oriented Scalable Business Architecture** — a rich business object framework with rule-based validation, property change tracking, state management, and reflection-driven factories.

#### Business Object Hierarchy

```
BusinessObject<B>          — Core: rules, context, property management
    └── ObservableObject<T>  — Change tracking: NEW / CHANGED / DELETED state
        ├── EditableObject<T>  — Savable with async rule validation
        ├── ReadOnlyObject<T>  — Immutable with permission-based access
        └── ExecutableObject<T> — Template-based operation execution
```

#### Key Concepts

| Concept | Description |
|---------|-------------|
| **BusinessContext** | Service locator and object factory holder; injects context and initializes rules |
| **PropertyInfo<T>** | Typed property metadata: name, type, friendly name, default value, field reference |
| **FieldDataManager** | Per-instance reflection-based field value management |
| **Rule System** | Async rule validation with `RuleManager` (per-type singleton) & `Rules` (per-instance executor) |
| **ObjectEditState** | Lifecycle state machine: `NONE → NEW → CHANGED → DELETED` |
| **ObjectFactory** | Reflection-driven CRUD factory: `@FactoryCreate`, `@FactoryFetch`, `@FactoryInsert`, `@FactoryUpdate`, `@FactoryDelete`, `@FactoryExecute` |

#### Rule System

```java
protected void addRules() {
    getRules().addRule(new LambdaRule<>(age, (a, ctx) -> a != null && a >= 18, "Must be 18+"));
}
```

| Class | Description |
|-------|-------------|
| `Rule` | Interface: `getName()`, `getProperty()`, `getPriority()`, `executeAsync(RuleContext)` |
| `LambdaRule<T>` | Lambda-based: `(value, context) → boolean` |
| `RegularRule` | Method-based execution |
| `RequiredRule` | Non-null property validation |
| `BrokenRule` / `BrokenRuleCollection` | Validation result with severity (ERROR, WARNING, INFO) |
| `RuleCheckException` | Thrown on validation failure |

---

## Sample Application

The `sample` module demonstrates **Euonia framework integration with Spring Boot 4.0**:

| Component | Description |
|-----------|-------------|
| **`User` aggregate** | `EditableObject<User>` with `@FactoryCreate`, custom rules (`UserNameRule`, `LambdaRule`), and Snowflake ID generation |
| **`OsbaConfiguration`** | Wires `BusinessObjectFactory` with Spring's `ApplicationContext` |
| **`UserController`** | REST API: `POST /api/user`, `GET /api/user/{id}` — using `ObjectFactory` to create/fetch aggregates |

### Tech Stack

| Category | Technology |
|----------|-----------|
| **Language** | Java 17+ (sample uses Java 25) |
| **Framework** | Spring Boot 4.0 (Spring MVC, Spring Data JPA, Spring Framework 7.0) |
| **Database** | MySQL, H2 (in-memory for testing) |
| **API Docs** | SpringDoc OpenAPI 3.0 |
| **Build** | Maven |
| **ID Generation** | Snowflake, UUID, ULID |
| **Pipeline** | Custom middleware pipeline (chain-of-responsibility / middleware pattern) |
| **DI Integration** | Spring `ApplicationContext` via `ServiceProvider` abstraction |

---

## Quick Start

### Maven Dependencies

```xml
<!-- Core utilities -->
<dependency>
    <groupId>com.euonia</groupId>
    <artifactId>core</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- Pipeline middleware -->
<dependency>
    <groupId>com.euonia</groupId>
    <artifactId>pipeline</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- Spring Integration -->
<dependency>
    <groupId>com.euonia</groupId>
    <artifactId>spring</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- Business objects (OSBA) -->
<dependency>
    <groupId>com.euonia</groupId>
    <artifactId>osba</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- Domain-Driven Design -->
<dependency>
    <groupId>com.euonia</groupId>
    <artifactId>domain-driven-design</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- Message Bus (abstractions) -->
<dependency>
    <groupId>com.euonia</groupId>
    <artifactId>bus-abstract</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- Message Bus (core runtime) -->
<dependency>
    <groupId>com.euonia</groupId>
    <artifactId>bus-core</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- Message Bus (RabbitMQ transport) -->
<dependency>
    <groupId>com.euonia</groupId>
    <artifactId>bus-rabbitmq</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- Message Bus (Kafka transport) -->
<dependency>
    <groupId>com.euonia</groupId>
    <artifactId>bus-kafka</artifactId>
    <version>1.0.0</version>
</dependency>
```

```java
// Define a business object
@Component @Scope("prototype")
public class Order extends EditableObject<Order> {
    private final PropertyInfo<String> productName = registerProperty(String.class, "productName");

    @FactoryCreate
    protected void create(String productName) {
        super.create();
        setProductName(productName);
        setId(ObjectId.snowflake().getValue(Long.class));
    }

    @Override
    protected void addRules() {
        getRules().addRule(new RequiredRule(productName));
    }
}

// Use the factory
@Autowired
private ObjectFactory factory;

var order = factory.create(Order.class, "Widget");
order.save(false);
```

---

## Build

```bash
# Build all modules
mvn clean install

# Run the sample application
cd sample
mvn spring-boot:run
```

---

## Project Links

- **GitHub**: [github.com/NerosoftDev/euonia-java](https://github.com/NerosoftDev/euonia-java)
- **.NET Edition**: [github.com/NerosoftDev/Euonia](https://github.com/NerosoftDev/Euonia)

---

## Donate

<img alt="donate" width="512" src="https://qiniu-cdn.zhaorong.pro/images/donate.png" />

---

[![JetBrains](https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg)](https://www.jetbrains.com/)

Thanks to [JetBrains](https://www.jetbrains.com/) for supporting the project through [All Products Packs](https://www.jetbrains.com/products.html) within their [Free Open Source License](https://www.jetbrains.com/community/opensource) program.

---

![Alt](https://repobeats.axiom.co/api/embed/5dc93c910fbd2dc550495a9325f7bcd0235a6082.svg "Repobeats analytics image")
