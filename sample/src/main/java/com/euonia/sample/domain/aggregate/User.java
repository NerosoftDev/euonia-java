package com.euonia.sample.domain.aggregate;

import com.euonia.annotation.Required;
import com.euonia.core.ObjectId;
import com.euonia.factory.annotation.FactoryCreate;
import com.euonia.osba.rules.LambdaRule;
import com.euonia.osba.rules.RuleBase;
import com.euonia.osba.rules.RuleContext;
import com.euonia.reflection.DisplayName;
import com.euonia.reflection.PropertyInfo;
import com.euonia.sample.domain.EditableObjectBase;
import com.euonia.sample.domain.event.UserCreatedEvent;
import com.euonia.sample.persistent.UserRepository;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class User extends EditableObjectBase<User, Long> {

    private final PropertyInfo<Long> id = registerProperty(Long.class, "id");
    @DisplayName("User Name")
    @Required(message = "name is valid")
    private final PropertyInfo<String> name = registerProperty(String.class, "name");

    @DisplayName("User age")
    private final PropertyInfo<Integer> age = registerProperty(Integer.class, "age");

    @Override
    public Long getId() {
        return getProperty(id);
    }

    @Override
    public void setId(Long id) {
        loadProperty(this.id, id);
    }

    public String getName() {
        return getProperty(this.name);
    }

    public void setName(String name) {
        setProperty(this.name, name);
    }

    public int getAge() {
        return getProperty(age);
    }

    public void setAge(int age) {
        setProperty(this.age, age);
    }

    @Override
    protected void addRules() {
        super.addRules();
        getRules().addRule(new UserNameRule(this.name));
        getRules().addRule(new LambdaRule<>(this.age, (age, context) -> age != null && age >= 18, "Age must be at least 18"));
    }

    @FactoryCreate
    protected void create(String name) {
        super.create();
        setName(name);
        setId(Objects.requireNonNull(ObjectId.snowflake().getValue(Long.class)));
        raiseEvent(new UserCreatedEvent(getId(), name));
    }

    @Override
    protected void insert() {
        super.insert();
        var repository = getBusinessContext().getOrCreateObject(UserRepository.class);
    }

    protected void fetch(long id) {
        try (var x = bypassRuleChecks()) {
            setName("Test User");
        } catch (Exception ex) {
            //
        }
    }

    public class UserNameRule extends RuleBase {

        public UserNameRule(PropertyInfo<?> property) {
            super(property);
        }

        @Override
        public CompletableFuture<Void> executeAsync(RuleContext context) {
            return CompletableFuture.runAsync(() -> {

                if (!(context.getTarget() instanceof User user)) {
                    return;
                }

                var name = user.getName();

                if (name == null || name.trim().isEmpty()) {
                    context.addErrorResult(String.format("%s cannot be empty", getProperty().getFriendlyName()));
                } else if (name.length() < 12) {
                    context.addErrorResult(String.format("%s must be 12 characters", getProperty().getFriendlyName()));
                }
            });
        }
    }
}
