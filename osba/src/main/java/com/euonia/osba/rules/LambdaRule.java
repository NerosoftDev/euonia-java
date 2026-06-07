package com.euonia.osba.rules;

import com.euonia.osba.BusinessObject;
import com.euonia.reflection.PropertyInfo;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * LambdaRule allows defining a validation rule using a lambda function, providing flexibility to implement custom validation logic for a specific property of a business object.
 *
 * @param <T> the type of the property value being validated
 */
public class LambdaRule<T> extends RuleBase {
    private final PropertyInfo<T> property;
    private final BiFunction<T, RuleContext, Boolean> function;
    private final Function<T, String> messageFactory;

    public LambdaRule(PropertyInfo<T> property, BiFunction<T, RuleContext, Boolean> function, String message) {
        this(property, function, x -> message);
    }

    public LambdaRule(PropertyInfo<T> property, BiFunction<T, RuleContext, Boolean> function, Function<T, String> messageFactory) {
        super(property);
        this.property = property;
        this.function = function;
        this.messageFactory = messageFactory;
    }

    @Override
    public CompletableFuture<Void> executeAsync(RuleContext context) {
        try {
            if (context.getTarget() instanceof BusinessObject<?> businessObject) {

                T value = businessObject.readProperty(property);

                if (!function.apply(value, context)) {
                    context.addErrorResult(messageFactory.apply(value));
                }
            }
        } catch (Exception exception) {
            context.addErrorResult(exception.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }
}
