package com.euonia.reflection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("com.euonia.reflection.DisplayName")
class DisplayNameTest {

    private static class Sample {
        @com.euonia.reflection.DisplayName("User Name")
        private String username;
    }

    @Test
    @DisplayName("Given annotation definition when inspecting metadata then target is field and retention is runtime")
    void givenAnnotationDefinitionWhenInspectingThenTargetAndRetentionMatch() {
        Target target = com.euonia.reflection.DisplayName.class.getAnnotation(Target.class);
        Retention retention = com.euonia.reflection.DisplayName.class.getAnnotation(Retention.class);

        assertNotNull(target);
        assertArrayEquals(new ElementType[]{ElementType.FIELD}, target.value());
        assertNotNull(retention);
        assertEquals(java.lang.annotation.RetentionPolicy.RUNTIME, retention.value());
    }

    @Test
    @DisplayName("Given field with annotation when reading then display name value is available")
    void givenAnnotatedFieldWhenReadingThenDisplayNameValueIsAvailable() throws NoSuchFieldException {
        var field = Sample.class.getDeclaredField("username");
        com.euonia.reflection.DisplayName annotation = field.getAnnotation(com.euonia.reflection.DisplayName.class);

        assertNotNull(annotation);
        assertEquals("User Name", annotation.value());
    }
}

