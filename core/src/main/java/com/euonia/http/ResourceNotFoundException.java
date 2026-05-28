package com.euonia.http;

/**
 * The ResourceNotFoundException class represents an HTTP 404 Not Found error.
 * It extends the HttpStatusException class and provides constructors for creating instances of this exception with a specific message and an optional cause.
 */
public class ResourceNotFoundException extends HttpStatusException {
    /**
     * Creates a new ResourceNotFoundException with the specified message.
     * This constructor allows for creating an exception with a specific message, providing more detailed error information when the exception is thrown.
     *
     * @param message The detail message for the exception.
     */
    public ResourceNotFoundException(String message) {
        super(404, message);
    }

    /**
     * Creates a new ResourceNotFoundException with the specified message and cause.
     * This constructor allows for creating an exception with a specific message and cause, providing more detailed error information when the exception is thrown.
     *
     * @param message The detail message for the exception.
     * @param cause The cause of the exception.
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(404, message, cause);
    }
}
