package com.euonia.security;

/**
 * The UnauthorizedAccessException is thrown when an attempt is made to access a resource or perform an action without proper authentication or authorization.
 * This exception indicates that the user does not have the necessary credentials or permissions to access the requested resource.
 * It is typically used in scenarios where authentication is required but has not been provided, or when the provided credentials are invalid.
 */
@SuppressWarnings("unused")
public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }

    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
