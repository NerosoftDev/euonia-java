package com.euonia.http;

/**
 * The ServiceUnavailableException class represents an HTTP 503 Service Unavailable error.
 * It extends the HttpStatusException class and provides constructors for creating instances of this exception with a specific message and an optional cause.
 */
public class ServiceUnavailableException extends HttpStatusException {
    /**
     * Creates a new ServiceUnavailableException with the specified message.
     * This constructor allows for creating an exception with a specific message, providing more detailed error information when the exception is thrown.
     *
     * @param message The detail message for the exception.
     */
    public ServiceUnavailableException(String message) {
        super(503, message);
    }

    /**
     * Creates a new ServiceUnavailableException with the specified message and cause.
     * This constructor allows for creating an exception with a specific message and cause, providing more detailed error information when the exception is thrown.
     *
     * @param message The detail message for the exception.
     * @param cause The cause of the exception.
     */
    public ServiceUnavailableException(String message, Throwable cause) {
        super(503, message, cause);
    }
}
