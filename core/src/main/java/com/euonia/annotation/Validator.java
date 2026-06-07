package com.euonia.annotation;

import com.euonia.tuple.Duet;

import java.lang.annotation.Annotation;

public interface Validator<A extends Annotation> {
    Duet<Boolean, String> validate(A annotation, Object value);
}
