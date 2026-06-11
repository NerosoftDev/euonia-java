# OSBA Module

Object-Oriented Scalable Business Architecture — the core business object framework of Euonia. Provides a rich hierarchy of business objects with built-in rule-based validation, property change tracking, object lifecycle management, and annotation-driven factory patterns.

---

## Architecture

```
                    ┌──────────────────────────┐
                    │     BusinessObject<B>      │
                    │  ┌──────────────────────┐ │
                    │  │ Rule management       │ │  ← DataAnnotationRule, LambdaRule
                    │  │ BusinessContext        │ │  ← Service resolution
                    │  │ PropertyChangeSupport  │ │  ← Observer pattern
                    │  │ FieldDataManager       │ │  ← Reflection-based fields
                    │  └──────────────────────┘ │
                    └───────────┬──────────────┘
                                │
          ┌─────────────────────┼─────────────────────┐
          │                     │                     │
          ▼                     ▼                     ▼
┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐
│ ReadOnlyObject   │   │ObservableObject  │   │ ExecutableObject │
│  (immutable)     │   │  (track state)   │   │  (operations)    │
└─────────────────┘   └────────┬────────┘   └─────────────────┘
                               │
                               ▼
                      ┌─────────────────┐
                      │  EditableObject  │
                      │   (savable)      │
                      └─────────────────┘

        BusinessObjectFactory
                │
                ├── @FactoryCreate   → new instance
                ├── @FactoryFetch    → retrieve from persistence
                ├── @FactoryInsert   → persist new
                ├── @FactoryUpdate   → persist changes
                ├── @FactoryDelete   → remove from persistence
                └── @FactoryExecute  → execute operation
```

---

## Core Concepts

### Business Object Hierarchy

| Class | Description |
|-------|-------------|
| `BusinessObject<B>` | Core base class — rule management, `BusinessContext` integration, property change support, permission checks, reflection-based field data |
| `ObservableObject<T>` | Tracks edit state (`NONE`, `NEW`, `CHANGED`, `DELETED`), busy counter, and exposes `Flow.Publisher` for async observation |
| `EditableObject<T>` | Extends `ObservableObject` with `Savable` interface — async rule-checked save, save events, delete tracking |
| `ReadOnlyObject<T>` | Immutability-focused — disables write access and bypasses default rules |
| `ExecutableObject<T>` | For operation-oriented objects — provides `execute()` and `create()` methods |

### Rule System

| Class / Interface | Description |
|-------------------|-------------|
| `Rule` | Interface for validation rules with priority ordering |
| `RuleManager` | Per-type singleton rule registry (`ConcurrentHashMap`) |
| `Rules` | Instance-level async rule executor using `CompletableFuture` |
| `RuleBase` | Abstract base for custom rules |
| `LambdaRule` | Lambda-based rule execution |
| `DataAnnotationRule` | Annotation-driven rule — `@Required`, `@Validator` |
| `BrokenRule` | Rule violation with `property`, `severity`, `description` |
| `BrokenRuleCollection` | Tracks errors, warnings, and info-level violations |
| `RuleContext` | Execution context passed to rules during validation |

### Factory Pattern

| Annotation | Lifecycle Method |
|------------|-----------------|
| `@FactoryCreate` | Create a new object instance |
| `@FactoryFetch` | Retrieve from persistence by ID |
| `@FactoryInsert` | Persist a new object |
| `@FactoryUpdate` | Persist changes to an existing object |
| `@FactoryDelete` | Remove from persistence |
| `@FactoryExecute` | Execute a custom operation |

`BusinessObjectFactory` uses reflection to discover annotated factory methods at runtime, supporting both Spring `ApplicationContext` and custom bean factories.

### Reflection System

| Class | Description |
|-------|-------------|
| `PropertyInfo<T>` | Typed property metadata — name, type, friendly name, default value |
| `FieldData` | Instance property value storage |
| `FieldDataManager` | Manages all field data for a single object instance |
| `PropertyInfoManager` | Registry of `PropertyInfo` for a given type |
| `ObjectReflector` | Static utilities for factory method lookup with caching |

### Key Interfaces

| Interface | Description |
|-----------|-------------|
| `RuleCheckable` | `isValid()`, `getBrokenRules()`, `ruleCheckComplete()` |
| `TrackableObject` | `isValid()`, `isChanged()`, `isDeleted()`, `isNew()`, `isSavable()`, `isBusy()` |
| `Savable<T>` | `save(forceUpdate)`, `saveComplete()` |
| `OperableProperty` | `getProperty()`, `setProperty()` with typed `PropertyInfo` |
| `UseBusinessContext` | `setBusinessContext()`, `getBusinessContext()` |

### BusinessContext

- Provides service resolution for business objects
- Manages instance creation via `Function<Class<?>, ?>`
- Typically wired from Spring `ApplicationContext` or custom DI container
- Integrates with `BusinessObjectFactory`

---

## Design Patterns

| Pattern | Usage |
|---------|-------|
| **Factory** | `BusinessObjectFactory` with reflection-based annotated method lookup |
| **Observer** | `PropertyChangeSupport`, `Flow.Publisher` for reactive property changes |
| **Template Method** | Abstract lifecycle hooks: `addRules()`, `initialize()`, `onBusinessContextSet()` |
| **Singleton** | `RuleManager` per type, field data managers per object |
| **Strategy** | Pluggable rule types — `LambdaRule`, `DataAnnotationRule`, custom `RuleBase` |

---

## Dependencies

- `core` — ID generation, reflection utilities, annotations, tuple types

---

## Quick Start

See the full example in the [`sample`](../sample) module. Below is a condensed view of `sample/.../domain/aggregate/User.java`:

```java
public class User extends EditableObjectBase<User, Long> {

    private final PropertyInfo<Long> id = registerProperty(Long.class, "id");

    @DisplayName("User Name")
    @Required(message = "name is valid")
    private final PropertyInfo<String> name = registerProperty(String.class, "name");

    @DisplayName("User age")
    private final PropertyInfo<Integer> age = registerProperty(Integer.class, "age");

    public Long getId() { return getProperty(id); }
    public void setId(Long id) { loadProperty(this.id, id); }

    public String getName() { return getProperty(this.name); }
    public void setName(String name) { setProperty(this.name, name); }

    public int getAge() { return getProperty(age); }
    public void setAge(int age) { setProperty(this.age, age); }

    @Override
    protected void addRules() {
        super.addRules();
        getRules().addRule(new UserNameRule(this.name));
        getRules().addRule(new LambdaRule<>(this.age,
            (age, context) -> age != null && age >= 18,
            "Age must be at least 18"));
    }

    @FactoryCreate
    protected void create(String name) {
        super.create();
        setName(name);
        setId(Objects.requireNonNull(ObjectId.snowflake().getValue(Long.class)));
        raiseEvent(new UserCreatedEvent(getId(), name));
    }

    // Nested custom rule
    public class UserNameRule extends RuleBase {
        public UserNameRule(PropertyInfo<?> property) { super(property); }

        @Override
        public CompletableFuture<Void> executeAsync(RuleContext context) {
            return CompletableFuture.runAsync(() -> {
                if (!(context.getTarget() instanceof User user)) return;
                var name = user.getName();
                if (name == null || name.trim().isEmpty()) {
                    context.addErrorResult(
                        String.format("%s cannot be empty", getProperty().getFriendlyName()));
                } else if (name.length() < 12) {
                    context.addErrorResult(
                        String.format("%s must be 12 characters", getProperty().getFriendlyName()));
                }
            });
        }
    }
}
```
