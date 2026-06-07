package com.euonia.annotation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Validation annotation")
class ValidationAnnotationTest {

    @Test
    @DisplayName("Given Validation definition when inspecting metadata then target and retention are correct")
    void givenValidationDefinitionWhenInspectingThenMetadataIsCorrect() {
        Target target = Validation.class.getAnnotation(Target.class);
        Retention retention = Validation.class.getAnnotation(Retention.class);

        assertNotNull(target);
        assertArrayEquals(new ElementType[]{ElementType.ANNOTATION_TYPE}, target.value());
        assertNotNull(retention);
        assertEquals(java.lang.annotation.RetentionPolicy.RUNTIME, retention.value());
    }

    @Test
    @DisplayName("Given Required annotation when reading Validation meta then validator type is RequiredValidator")
    void givenRequiredAnnotationWhenReadingValidationMetaThenValidatorTypeMatches() {
        Validation validation = Required.class.getAnnotation(Validation.class);

        assertNotNull(validation);
        assertEquals(RequiredValidator.class, validation.validator());
    }
}

