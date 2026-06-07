package com.euonia.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Authentication and authorization exceptions")
class AuthExceptionsTest {

    @Test
    @DisplayName("Given message and cause when constructing AuthenticationException then properties are preserved")
    void givenMessageAndCauseWhenConstructingAuthenticationExceptionThenPropertiesArePreserved() {
        Throwable cause = new RuntimeException("cause");

        AuthenticationException oneArg = new AuthenticationException("auth failed");
        AuthenticationException twoArg = new AuthenticationException("auth failed", cause);

        assertEquals("auth failed", oneArg.getMessage());
        assertNull(oneArg.getCause());
        assertEquals("auth failed", twoArg.getMessage());
        assertSame(cause, twoArg.getCause());
    }

    @Test
    @DisplayName("Given message and cause when constructing UnauthorizedAccessException then properties are preserved")
    void givenMessageAndCauseWhenConstructingUnauthorizedAccessExceptionThenPropertiesArePreserved() {
        Throwable cause = new RuntimeException("cause");

        UnauthorizedAccessException oneArg = new UnauthorizedAccessException("forbidden");
        UnauthorizedAccessException twoArg = new UnauthorizedAccessException("forbidden", cause);

        assertEquals("forbidden", oneArg.getMessage());
        assertNull(oneArg.getCause());
        assertEquals("forbidden", twoArg.getMessage());
        assertSame(cause, twoArg.getCause());
    }
}

