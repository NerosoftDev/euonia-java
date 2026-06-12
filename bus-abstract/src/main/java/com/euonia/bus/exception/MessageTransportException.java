package com.euonia.bus.exception;

/**
 * Represents an exception that occurs during message transport in the bus system.
 * This exception is thrown when there is an issue with sending or receiving messages,
 * such as network errors, serialization issues, or other transport-related problems.
 */
public class MessageTransportException extends RuntimeException {
    /**
     * Constructs a new MessageTransportException with the specified detail message.
     *
     * @param message the detail message
     */
    public MessageTransportException(String message) {
        super(message);
    }

    /**
     * Constructs a new MessageTransportException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public MessageTransportException(String message, Throwable cause) {
        super(message, cause);
    }
}
