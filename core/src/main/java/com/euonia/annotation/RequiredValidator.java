package com.euonia.annotation;

import com.euonia.tuple.Duet;

public class RequiredValidator implements Validator<Required> {
    @Override
    public Duet<Boolean, String> validate(Required annotation, Object value) {
        Duet<Boolean, String> result;
        if (value == null) {
            result = new Duet<>(false, annotation.message().isEmpty() ? "Value is required" : annotation.message());
        } else if (!annotation.allowEmpty() && value instanceof String string && string.isEmpty()) {
            result = new Duet<>(false, annotation.message().isEmpty() ? "Value must not be empty" : annotation.message());
        } else {
            result = new Duet<>(true, "");
        }
        return result;
    }
}
