package com.euonia.osba.rules;

import com.euonia.osba.BusinessObject;
import com.euonia.reflection.PropertyInfo;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class RegularRule extends RuleBase {

    private final PropertyInfo<String> property;
    private final Function<String, String> messageFactory;
    private final String expression;

    private boolean ignoreNullValue = true;

    public RegularRule(PropertyInfo<String> property, String expression, String message) {
        this(property, expression, x -> message);
    }

    public RegularRule(PropertyInfo<String> property, String expression, Function<String, String> messageFactory) {
        this.property = property;
        this.expression = expression;
        this.messageFactory = messageFactory;
    }

    public String getExpression() {
        return expression;
    }

    public boolean isIgnoreNullValue() {
        return ignoreNullValue;
    }

    public void setIgnoreNullValue(boolean ignoreNullValue) {
        this.ignoreNullValue = ignoreNullValue;
    }

    @Override
    public CompletableFuture<Void> executeAsync(RuleContext context) {
        try {
            if (context.getTarget() instanceof BusinessObject<?> businessObject) {

                String value = businessObject.readProperty(property);

                if (value == null || value.isEmpty()) {
                    if (ignoreNullValue) {
                        context.addErrorResult(messageFactory.apply(value));
                    }
                } else {
                    if (!value.matches(expression)) {
                        context.addErrorResult(messageFactory.apply(value));
                    }
                }
            }
        } catch (Exception exception) {
            context.addErrorResult(exception.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }
}
