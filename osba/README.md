# OSBA 模块

面向对象的可扩展业务架构（Object-Oriented Scalable Business Architecture）—— Euonia 的核心业务对象框架。提供丰富的业务对象层次结构，内置基于规则的验证、属性变更追踪、对象生命周期管理和注解驱动的工厂模式。

---

## 架构

```
                    ┌──────────────────────────┐
                    │     BusinessObject<B>      │
                    │  ┌──────────────────────┐ │
                    │  │ 规则管理               │ │  ← DataAnnotationRule、LambdaRule
                    │  │ BusinessContext        │ │  ← 服务解析
                    │  │ PropertyChangeSupport  │ │  ← 观察者模式
                    │  │ FieldDataManager       │ │  ← 基于反射的字段管理
                    │  └──────────────────────┘ │
                    └───────────┬──────────────┘
                                │
          ┌─────────────────────┼─────────────────────┐
          │                     │                     │
          ▼                     ▼                     ▼
┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐
│ ReadOnlyObject   │   │ObservableObject  │   │ ExecutableObject │
│  （不可变对象）    │   │  （状态追踪）     │   │  （操作型对象）   │
└─────────────────┘   └────────┬────────┘   └─────────────────┘
                               │
                               ▼
                      ┌─────────────────┐
                      │  EditableObject  │
                      │   （可保存对象）   │
                      └─────────────────┘

        BusinessObjectFactory
                │
                ├── @FactoryCreate   → 创建新实例
                ├── @FactoryFetch    → 从持久层检索
                ├── @FactoryInsert   → 持久化新增
                ├── @FactoryUpdate   → 持久化变更
                ├── @FactoryDelete   → 从持久层删除
                └── @FactoryExecute  → 执行操作
```

---

## 核心概念

### 业务对象层次结构

| 类 | 描述 |
|----|------|
| `BusinessObject<B>` | 核心基类 — 规则管理、`BusinessContext` 集成、属性变更支持、权限检查、基于反射的字段数据管理 |
| `ObservableObject<T>` | 追踪编辑状态（`NONE`、`NEW`、`CHANGED`、`DELETED`）、忙碌计数器，并通过 `Flow.Publisher` 暴露异步观察能力 |
| `EditableObject<T>` | 扩展 `ObservableObject`，实现 `Savable` 接口 — 异步规则校验保存、保存事件、删除追踪 |
| `ReadOnlyObject<T>` | 面向不可变对象 — 禁用写访问并绕过默认规则 |
| `ExecutableObject<T>` | 面向操作型对象 — 提供 `execute()` 和 `create()` 方法 |

### 规则系统

| 类 / 接口 | 描述 |
|-----------|------|
| `Rule` | 带优先级的验证规则接口 |
| `RuleManager` | 每个类型的单例规则注册表（`ConcurrentHashMap`） |
| `Rules` | 实例级异步规则执行器，基于 `CompletableFuture` |
| `RuleBase` | 自定义规则的抽象基类 |
| `LambdaRule` | 基于 Lambda 表达式的规则执行 |
| `DataAnnotationRule` | 注解驱动的规则 — `@Required`、`@Validator` |
| `BrokenRule` | 规则违规，包含 `property`、`severity`、`description` |
| `BrokenRuleCollection` | 追踪错误、警告和信息级别的违规 |
| `RuleContext` | 验证期间传递给规则的执行上下文 |

### 工厂模式

| 注解 | 生命周期方法 |
|------|-------------|
| `@FactoryCreate` | 创建新对象实例 |
| `@FactoryFetch` | 按 ID 从持久层检索 |
| `@FactoryInsert` | 持久化新对象 |
| `@FactoryUpdate` | 持久化对象变更 |
| `@FactoryDelete` | 从持久层删除 |
| `@FactoryExecute` | 执行自定义操作 |

`BusinessObjectFactory` 通过反射在运行时发现带注解的工厂方法，同时支持 Spring `ApplicationContext` 和自定义 Bean 工厂。

### 反射系统

| 类 | 描述 |
|----|------|
| `PropertyInfo<T>` | 类型化属性元数据 — 名称、类型、友好名称、默认值 |
| `FieldData` | 实例属性值存储 |
| `FieldDataManager` | 管理单个对象实例的所有字段数据 |
| `PropertyInfoManager` | 给定类型的 `PropertyInfo` 注册表 |
| `ObjectReflector` | 带缓存的工厂方法查找静态工具类 |

### 关键接口

| 接口 | 描述 |
|------|------|
| `RuleCheckable` | `isValid()`、`getBrokenRules()`、`ruleCheckComplete()` |
| `TrackableObject` | `isValid()`、`isChanged()`、`isDeleted()`、`isNew()`、`isSavable()`、`isBusy()` |
| `Savable<T>` | `save(forceUpdate)`、`saveComplete()` |
| `OperableProperty` | 带类型化 `PropertyInfo` 的 `getProperty()`、`setProperty()` |
| `UseBusinessContext` | `setBusinessContext()`、`getBusinessContext()` |

### BusinessContext

- 为业务对象提供服务解析能力
- 通过 `Function<Class<?>, ?>` 管理实例创建
- 通常从 Spring `ApplicationContext` 或自定义 DI 容器注入
- 与 `BusinessObjectFactory` 集成

---

## 设计模式

| 模式 | 使用场景 |
|------|---------|
| **工厂模式** | `BusinessObjectFactory` 基于反射的注解方法查找 |
| **观察者模式** | `PropertyChangeSupport`、`Flow.Publisher` 实现响应式属性变更 |
| **模板方法** | 抽象生命周期钩子：`addRules()`、`initialize()`、`onBusinessContextSet()` |
| **单例模式** | 按类型的 `RuleManager`、按对象的字段数据管理器 |
| **策略模式** | 可插拔的规则类型 — `LambdaRule`、`DataAnnotationRule`、自定义 `RuleBase` |

---

## 依赖

- `core` — ID 生成、反射工具、注解、元组类型

---

## 快速开始

完整示例请参见 [`sample`](../sample) 模块。以下是 `sample/.../domain/aggregate/User.java` 的精简视图：

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

    // 嵌套自定义规则
    public class UserNameRule extends RuleBase {
        public UserNameRule(PropertyInfo<?> property) { super(property); }

        @Override
        public CompletableFuture<Void> executeAsync(RuleContext context) {
            return CompletableFuture.runAsync(() -> {
                if (!(context.getTarget() instanceof User user)) return;
                var name = user.getName();
                if (name == null || name.trim().isEmpty()) {
                    context.addErrorResult(
                        String.format("%s 不能为空", getProperty().getFriendlyName()));
                } else if (name.length() < 12) {
                    context.addErrorResult(
                        String.format("%s 必须为 12 个字符", getProperty().getFriendlyName()));
                }
            });
        }
    }
}
```
