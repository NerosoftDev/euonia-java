package com.euonia.osba.rules;

import com.euonia.annotation.Validation;
import com.euonia.annotation.Validator;
import com.euonia.osba.BusinessObject;
import com.euonia.reflection.PropertyInfo;
import com.euonia.tuple.Duet;

import java.lang.annotation.Annotation;
import java.util.concurrent.CompletableFuture;

public class DataAnnotationRule<A extends Annotation> extends RuleBase {

    private final A annotation;

    public DataAnnotationRule(PropertyInfo<?> property, A annotation) {
        super(property);
        assert annotation != null : "Annotation cannot be null.";
        this.annotation = annotation;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CompletableFuture<Void> executeAsync(RuleContext context) {
        try {
            if (context.getTarget() instanceof BusinessObject<?> businessObject) {

                var value = businessObject.readProperty(getProperty());

                var field = getProperty().getField();
                if (field != null) {
                    var validatorAnnotation = annotation.annotationType().getAnnotation(Validation.class);
                    if (validatorAnnotation != null) {
                        var validatorClass = validatorAnnotation.validator();
                        var validator = validatorClass.getDeclaredConstructor().newInstance();
                        Duet<Boolean, String> validate = ((Validator<A>) validator).validate(annotation, value);
                        if (!validate.value1()) {
                            context.addErrorResult(validate.value2());
                        }
                    }
                }
            }
        } catch (Exception exception) {
            context.addErrorResult(exception.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }
}
