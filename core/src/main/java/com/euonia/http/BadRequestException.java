package com.euonia.http;

/**
 * The BadRequestException class represents an HTTP 400 Bad Request error. It extends the HttpStatusException class and provides constructors for creating instances of this exception with a specific message and an optional cause.
 */
public class BadRequestException extends HttpStatusException {

    /**
     * Creates a new BadRequestException with the specified message. This constructor allows for creating an exception with a specific message, providing more detailed error information when the exception is thrown.
     *
     * @param message The detail message for the exception.
     */
    public BadRequestException(String message) {
        super(400, message);
    }

    /**
     * Creates a new BadRequestException with the specified message and cause. This constructor allows for creating an exception with a specific message and cause, providing more detailed error information when the exception is thrown.
     *
     * @param message The detail message for the exception.
     * @param cause The cause of the exception.
     */
    public BadRequestException(String message, Throwable cause) {
        super(400, message, cause);
    }
}
