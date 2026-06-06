package com.euonia.reflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify a display name for a field, which can be used in UI or error messages instead of the actual field name.
 * This is particularly useful for providing more user-friendly names for fields that may have technical or non-descriptive names in the code.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DisplayName {
    String value();
}
