package com.euonia.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ResponseHttpStatusCode")
class ResponseHttpStatusCodeTest {

    @ResponseHttpStatusCode(204)
    private static class AnnotatedType {
    }

    @Test
    @DisplayName("Given annotation definition when inspecting metadata then target is type and retention is runtime")
    void givenAnnotationDefinitionWhenInspectingThenTargetAndRetentionMatch() {
        Target target = ResponseHttpStatusCode.class.getAnnotation(Target.class);
        Retention retention = ResponseHttpStatusCode.class.getAnnotation(Retention.class);

        assertNotNull(target);
        assertArrayEquals(new ElementType[]{ElementType.TYPE}, target.value());
        assertNotNull(retention);
        assertEquals(java.lang.annotation.RetentionPolicy.RUNTIME, retention.value());
    }

    @Test
    @DisplayName("Given annotated class when reading annotation then status code value is available")
    void givenAnnotatedClassWhenReadingAnnotationThenStatusCodeValueIsAvailable() {
        ResponseHttpStatusCode annotation = AnnotatedType.class.getAnnotation(ResponseHttpStatusCode.class);

        assertNotNull(annotation);
        assertEquals(204, annotation.value());
    }
}

