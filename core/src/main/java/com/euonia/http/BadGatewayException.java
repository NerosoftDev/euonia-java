package com.euonia.http;

/**
 * The BadGatewayException is thrown when an HTTP request results in a 502 Bad Gateway status code.
 * This exception indicates that the server, while acting as a gateway or proxy, received an invalid response from the upstream server it accessed in attempting to fulfill the request.
 * It is typically used in scenarios where the server is acting as a gateway or proxy and encounters an error while trying to communicate with the upstream server, such as when the upstream server is down, overloaded, or returns an invalid response.
 * This exception provides constructors to create an exception with a specific message and an optional cause for the exception, allowing for more detailed error information to be included when the exception is thrown.
 */
public class BadGatewayException extends HttpStatusException {

    /**
     * Creates a new BadGatewayException with the specified message. This constructor allows for creating an exception with a specific message, providing more detailed error information when the exception is thrown.
     *
     * @param message The detail message for the exception.
     */
    public BadGatewayException(String message) {
        super(502, message);
    }

    /**
     * Creates a new BadGatewayException with the specified message and cause. This constructor allows for creating an exception with a specific message and cause, providing more detailed error information when the exception is thrown.
     *
     * @param message The detail message for the exception.
     * @param cause The cause of the exception.
     */
    public BadGatewayException(String message, Throwable cause) {
        super(502, message, cause);
    }
}
