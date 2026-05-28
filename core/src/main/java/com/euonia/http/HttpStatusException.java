package com.euonia.http;

/**
 * The HttpStatusException is thrown when an HTTP request results in an error status code.
 * It contains the HTTP status code and a message describing the error.
 * This exception can be used to represent various HTTP errors, such as 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found, 500 Internal Server Error, etc.
 * It provides constructors to create an exception with a specific status code and message, as well as an optional cause for the exception.
 */
public class HttpStatusException extends RuntimeException {
    private final int statusCode;

    public HttpStatusException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatusException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
