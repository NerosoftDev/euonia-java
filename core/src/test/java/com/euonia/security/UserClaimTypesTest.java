package com.euonia.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserClaimTypes")
class UserClaimTypesTest {

    @Test
    @DisplayName("Given claim constants when reading representative values then they match expected OIDC names")
    void givenClaimConstantsWhenReadingThenRepresentativeValuesMatch() {
        assertEquals("sub", UserClaimTypes.SUBJECT);
        assertEquals("name", UserClaimTypes.NAME);
        assertEquals("email", UserClaimTypes.EMAIL);
        assertEquals("role", UserClaimTypes.ROLE);
        assertEquals("tenant", UserClaimTypes.TENANT);
        assertEquals("scheme", UserClaimTypes.SCHEME);
    }

    @Test
    @DisplayName("Given claim constants when reflecting then all are public static final and values are unique")
    void givenClaimConstantsWhenReflectingThenModifiersAndUniquenessMatch() throws IllegalAccessException {
        Field[] fields = UserClaimTypes.class.getDeclaredFields();
        Set<String> values = new HashSet<>();
        int constantCount = 0;

        for (Field field : fields) {
            if (field.getType() == String.class) {
                int modifiers = field.getModifiers();
                assertTrue(Modifier.isPublic(modifiers));
                assertTrue(Modifier.isStatic(modifiers));
                assertTrue(Modifier.isFinal(modifiers));
                String value = (String) field.get(null);
                assertTrue(values.add(value), () -> "Duplicate claim value: " + value);
                constantCount++;
            }
        }

        assertTrue(constantCount >= 40);
    }
}

