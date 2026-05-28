package com.euonia.security;

/**
 * Exception thrown when authentication fails due to invalid credentials, expired tokens, or other authentication-related issues.
 */
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
