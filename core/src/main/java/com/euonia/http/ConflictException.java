package com.euonia.http;

/**
 * The ConflictException class represents an HTTP 409 Conflict error. It extends the HttpStatusException class and provides constructors for creating instances of this exception with a specific message and an optional cause.
 */
public class ConflictException extends HttpStatusException {

    /**
     * Creates a new ConflictException with the specified message. This constructor allows for creating an exception with a specific message, providing more detailed error information when the exception is thrown.
     *
     * @param message The detail message for the exception.
     */
    public ConflictException(String message) {
        super(409, message);
    }

    /**
     * Creates a new ConflictException with the specified message and cause. This constructor allows for creating an exception with a specific message and cause, providing more detailed error information when the exception is thrown.
     *
     * @param message The detail message for the exception.
     * @param cause The cause of the exception.
     */
    public ConflictException(String message, Throwable cause) {
        super(409, message, cause);
    }

}
