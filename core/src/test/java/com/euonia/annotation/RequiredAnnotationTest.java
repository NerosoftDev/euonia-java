package com.euonia.annotation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Required annotation")
class RequiredAnnotationTest {

    @Required
    private String sampleField;

    @Test
    @DisplayName("Given Required definition when inspecting metadata then target and retention are correct")
    void givenRequiredDefinitionWhenInspectingThenMetadataIsCorrect() {
        Target target = Required.class.getAnnotation(Target.class);
        Retention retention = Required.class.getAnnotation(Retention.class);
        Validation validation = Required.class.getAnnotation(Validation.class);

        assertNotNull(target);
        assertArrayEquals(new ElementType[]{ElementType.FIELD, ElementType.PARAMETER}, target.value());
        assertNotNull(retention);
        assertEquals(java.lang.annotation.RetentionPolicy.RUNTIME, retention.value());
        assertNotNull(validation);
        assertEquals(RequiredValidator.class, validation.validator());
    }

    @Test
    @DisplayName("Given default Required usage when reading annotation then default attributes are applied")
    void givenDefaultRequiredUsageWhenReadingThenDefaultAttributesApplied() throws NoSuchFieldException {
        Required annotation = RequiredAnnotationTest.class.getDeclaredField("sampleField").getAnnotation(Required.class);

        assertNotNull(annotation);
        assertFalse(annotation.allowEmpty());
        assertEquals("", annotation.message());
        assertEquals(Required.class, annotation.annotation());
    }
}

