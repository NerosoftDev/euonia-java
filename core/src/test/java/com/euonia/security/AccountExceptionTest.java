package com.euonia.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AccountException")
class AccountExceptionTest {

    private static class TestAccountException extends AccountException {
        TestAccountException(Object identity) {
            super(identity);
        }

        TestAccountException(Object identity, String message) {
            super(identity, message);
        }

        TestAccountException(Object identity, String message, Throwable cause) {
            super(identity, message, cause);
        }
    }

    @Test
    @DisplayName("Given identity and optional message cause when constructing then properties are preserved")
    void givenConstructorsWhenCreatingThenPropertiesArePreserved() {
        Throwable cause = new IllegalStateException("boom");

        TestAccountException oneArg = new TestAccountException("user1");
        TestAccountException twoArg = new TestAccountException("user2", "msg");
        TestAccountException threeArg = new TestAccountException("user3", "msg2", cause);

        assertEquals("user1", oneArg.getIdentity());
        assertNull(oneArg.getMessage());
        assertEquals("user2", twoArg.getIdentity());
        assertEquals("msg", twoArg.getMessage());
        assertEquals("user3", threeArg.getIdentity());
        assertEquals("msg2", threeArg.getMessage());
        assertSame(cause, threeArg.getCause());
    }

    @Test
    @DisplayName("Given details map when setting values then unsupported operation is thrown by immutable backing map")
    void givenDetailsWhenSettingThenThrowUnsupportedOperation() {
        TestAccountException exception = new TestAccountException("user");

        assertNotNull(exception.getDetails());
        assertTrue(exception.getDetails().isEmpty());
        assertNull(exception.get("missing"));
        assertThrows(UnsupportedOperationException.class, () -> exception.set("k", "v"));
        assertThrows(UnsupportedOperationException.class, () -> exception.with("k", "v"));
    }
}

