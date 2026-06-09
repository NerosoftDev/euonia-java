package com.euonia.osba.rules;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletableFuture;

import com.euonia.annotation.Validator;
import com.euonia.osba.BusinessObject;
import com.euonia.reflection.PropertyInfo;
import com.euonia.tuple.Duet;

/**
 * Represents a rule that is based on a specific annotation applied to a
 * property.
 * This class is designed to execute validation logic defined by the annotation
 * on the value of the property it is associated with.
 * The rule checks if the target object is a business object, retrieves the
 * value of the property, and then uses a validator specified by the annotation
 * to validate the value.
 * If the validation fails, an error result is added to the context with the
 * appropriate message.
 */
public class DataAnnotationRule<A extends Annotation> extends RuleBase {

    private final A annotation;
    private final Class<?> validatorType;

    /**
     * Creates a new instance of the DataAnnotationRule class with the specified
     * property and annotation.
     *
     * @param property      the property associated with the rule
     * @param annotation    the annotation defining the validation logic to be
     *                      applied
     * @param validatorType the class of the validator that will be used to validate
     *                      the value of the property against the annotation
     *                      to the property
     */
    public DataAnnotationRule(PropertyInfo<?> property, A annotation, Class<?> validatorType) {
        super(property);
        assert annotation != null : "Annotation cannot be null.";
        assert validatorType != null : "Validator type cannot be null.";
        this.annotation = annotation;
        this.validatorType = validatorType;
    }

    @Override
    public CompletableFuture<Void> executeAsync(RuleContext context) {
        try {
            if (context.getTarget() instanceof BusinessObject<?> businessObject) {

                var value = businessObject.readProperty(getProperty());

                var field = getProperty().getField();
                if (field != null) {
                    var validator = validatorType.getDeclaredConstructor().newInstance();
                    Duet<Boolean, String> validate = ((Validator<A>) validator).validate(annotation, value);
                    if (!validate.value1()) {
                        context.addErrorResult(validate.value2());
                    }
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException
                | InvocationTargetException exception) {
            context.addErrorResult(exception.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }
}
