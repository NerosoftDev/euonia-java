package com.euonia.osba.rules;

import com.euonia.osba.BusinessObject;
import com.euonia.reflection.PropertyInfo;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class RequiredRule extends RuleBase {

    private final PropertyInfo<?> property;
    private final Function<Object, String> messageFactory;

    public RequiredRule(PropertyInfo<?> property, String message) {
        this(property, x -> message);
    }

    public RequiredRule(PropertyInfo<?> property, Function<Object, String> messageFactory) {
        this.property = property;
        this.messageFactory = messageFactory;
    }

    @Override
    public CompletableFuture<Void> executeAsync(RuleContext context) {
        try {
            if (context.getTarget() instanceof BusinessObject<?> businessObject) {

                Object value = businessObject.readProperty(property);

                if (value == null || (value instanceof String string && string.isEmpty())) {
                    context.addErrorResult(messageFactory.apply(value));
                }
            }
        } catch (Exception exception) {
            context.addErrorResult(exception.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }
}
