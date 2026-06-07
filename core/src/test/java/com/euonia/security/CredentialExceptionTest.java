package com.euonia.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CredentialException")
class CredentialExceptionTest {

    private static class TestCredentialException extends CredentialException {
        TestCredentialException(Object credential) {
            super(credential);
        }

        TestCredentialException(Object credential, String message) {
            super(credential, message);
        }

        TestCredentialException(Object credential, String message, Throwable cause) {
            super(credential, message, cause);
        }
    }

    @Test
    @DisplayName("Given credential and optional message cause when constructing then properties are preserved")
    void givenConstructorsWhenCreatingThenPropertiesArePreserved() {
        Throwable cause = new IllegalArgumentException("invalid");

        TestCredentialException oneArg = new TestCredentialException("cred1");
        TestCredentialException twoArg = new TestCredentialException("cred2", "msg");
        TestCredentialException threeArg = new TestCredentialException("cred3", "msg2", cause);

        assertEquals("cred1", oneArg.getCredential());
        assertNull(oneArg.getMessage());
        assertEquals("cred2", twoArg.getCredential());
        assertEquals("msg", twoArg.getMessage());
        assertEquals("cred3", threeArg.getCredential());
        assertEquals("msg2", threeArg.getMessage());
        assertSame(cause, threeArg.getCause());
    }

    @Test
    @DisplayName("Given details map when setting values then unsupported operation is thrown by immutable backing map")
    void givenDetailsWhenSettingThenThrowUnsupportedOperation() {
        TestCredentialException exception = new TestCredentialException("credential");

        assertNotNull(exception.getDetails());
        assertTrue(exception.getDetails().isEmpty());
        assertNull(exception.get("missing"));
        assertThrows(UnsupportedOperationException.class, () -> exception.set("k", "v"));
        assertThrows(UnsupportedOperationException.class, () -> exception.with("k", "v"));
    }
}

