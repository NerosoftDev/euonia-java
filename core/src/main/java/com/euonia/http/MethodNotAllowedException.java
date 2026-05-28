package com.euonia.http;

/**
 * The MethodNotAllowedException class represents an HTTP 405 Method Not Allowed error.
 * It extends the HttpStatusException class and provides constructors for creating instances of this exception with a specific message and an optional cause.
 */
public class MethodNotAllowedException extends HttpStatusException {
    /**
     * Creates a new MethodNotAllowedException with the specified message. This constructor allows for creating an exception with a specific message, providing more detailed error information when the exception is thrown.
     *
     * @param message The detail message for the exception.
     */
    public MethodNotAllowedException(String message) {
        super(405, message);
    }

    /**
     * Creates a new MethodNotAllowedException with the specified message and cause. This constructor allows for creating an exception with a specific message and cause, providing more detailed error information when the exception is thrown.
     *
     * @param message The detail message for the exception.
     * @param cause The cause of the exception.
     */
    public MethodNotAllowedException(String message, Throwable cause) {
        super(405, message, cause);
    }
}
