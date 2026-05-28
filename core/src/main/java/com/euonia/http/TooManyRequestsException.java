package com.euonia.http;

/**
 * The TooManyRequestsException class represents an HTTP 429 Too Many Requests error.
 * It extends the HttpStatusException class and provides constructors for creating instances of this exception with a specific message and an optional cause.
 */
public class TooManyRequestsException extends HttpStatusException {
    /**
     * Creates a new TooManyRequestsException with the specified message.
     * This constructor allows for creating an exception with a specific message, providing more detailed error information when the exception is thrown.
     *
     * @param message The detail message for the exception.
     */
    public TooManyRequestsException(String message) {
        super(429, message);
    }

    /**
     * Creates a new TooManyRequestsException with the specified message and cause.
     * This constructor allows for creating an exception with a specific message and cause, providing more detailed error information when the exception is thrown.
     *
     * @param message The detail message for the exception.
     * @param cause The cause of the exception.
     */
    public TooManyRequestsException(String message, Throwable cause) {
        super(429, message, cause);
    }
}
