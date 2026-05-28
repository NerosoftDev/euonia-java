package com.euonia.http;

/**
 * The InternalServerErrorException class represents an HTTP 500 Internal Server Error.
 * It extends the HttpStatusException class and provides constructors for creating instances of this exception with a specific message and an optional cause.
 */
public class InternalServerErrorException extends HttpStatusException {
    /**
     * Creates a new InternalServerErrorException with the specified message. This constructor allows for creating an exception with a specific message, providing more detailed error information when the exception is thrown.
     *
     * @param message The detail message for the exception.
     */
    public InternalServerErrorException(String message) {
        super(500, message);
    }

    /**
     * Creates a new InternalServerErrorException with the specified message and cause. This constructor allows for creating an exception with a specific message and cause, providing more detailed error information when the exception is thrown.
     *
     * @param message The detail message for the exception.
     * @param cause The cause of the exception.
     */
    public InternalServerErrorException(String message, Throwable cause) {
        super(500, message, cause);
    }
}
