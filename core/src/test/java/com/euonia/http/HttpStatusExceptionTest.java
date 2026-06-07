package com.euonia.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HttpStatusException")
class HttpStatusExceptionTest {

    @Test
    @DisplayName("Given status and message when constructing then properties are preserved")
    void givenStatusAndMessageWhenConstructingThenPropertiesArePreserved() {
        HttpStatusException exception = new HttpStatusException(418, "teapot");

        assertEquals(418, exception.getStatusCode());
        assertEquals("teapot", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Given status message and cause when constructing then properties include cause")
    void givenStatusMessageAndCauseWhenConstructingThenPropertiesIncludeCause() {
        IllegalStateException cause = new IllegalStateException("broken");

        HttpStatusException exception = new HttpStatusException(500, "server error", cause);

        assertEquals(500, exception.getStatusCode());
        assertEquals("server error", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}

