package com.euonia.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Validation(validator = RequiredValidator.class)
public @interface Required {

    boolean allowEmpty() default false;

    String message() default "";

    Class<? extends Annotation> annotation() default Required.class;
}
