package com.euonia.http;
/**
 * The RequestTimeoutException class represents an HTTP 408 Request Timeout error.
 * It extends the HttpStatusException class and provides constructors for creating instances of this exception with a specific message and an optional cause.
 */
public class RequestTimeoutException extends HttpStatusException {
    /**
     * Creates a new RequestTimeoutException with the specified message.
     * This constructor allows for creating an exception with a specific message, providing more detailed error information when the exception is thrown.
     *
     * @param message The detail message for the exception.
     */
    public RequestTimeoutException(String message) {
        super(408, message);
    }

    /**
     * Creates a new RequestTimeoutException with the specified message and cause.
     * This constructor allows for creating an exception with a specific message and cause, providing more detailed error information when the exception is thrown.
     *
     * @param message The detail message for the exception.
     * @param cause The cause of the exception.
     */
    public RequestTimeoutException(String message, Throwable cause) {
        super(408, message, cause);
    }
}
