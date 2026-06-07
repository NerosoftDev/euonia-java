package com.euonia.reflection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GenericType")
class GenericTypeTest {

    @Test
    @DisplayName("Given anonymous generic subclass when creating then generic type argument is captured")
    void givenAnonymousGenericSubclassWhenCreatingThenTypeIsCaptured() {
        GenericType<List<String>> typeRef = new GenericType<>() {
        };

        Type type = typeRef.getType();

        assertEquals("java.util.List<java.lang.String>", type.getTypeName());
        assertTrue(typeRef.toString().contains("java.util.List<java.lang.String>"));
    }

    @Test
    @DisplayName("Given forType factory when creating wrappers then equals and hashCode depend on wrapped type")
    void givenForTypeFactoryWhenCreatingThenEqualsAndHashCodeDependOnType() {
        Type type = String.class;

        GenericType<String> left = GenericType.forType(type);
        GenericType<String> right = GenericType.forType(type);
        GenericType<Integer> other = GenericType.forType(Integer.class);

        assertEquals(left, right);
        assertEquals(left.hashCode(), right.hashCode());
        assertNotEquals(left, other);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    @DisplayName("Given raw GenericType subclass when instantiating then illegal argument is thrown")
    void givenRawSubclassWhenInstantiatingThenThrowIllegalArgument() {
        class RawGenericType extends GenericType {
        }

        assertThrows(IllegalArgumentException.class, RawGenericType::new);
    }
}

