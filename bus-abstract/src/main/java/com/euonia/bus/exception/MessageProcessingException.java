package com.euonia.bus.exception;

/**
 * Represents an exception that occurs during message processing in the bus system.
 * This exception is thrown when there is an issue with handling or processing messages,
 * such as validation errors, business logic errors, or other processing-related problems.
 */
public class MessageProcessingException extends RuntimeException {

    public MessageProcessingException() {
        super();
    }

    /**
     * Constructs a new MessageProcessingException with the specified detail message.
     *
     * @param message the detail message
     */
    public MessageProcessingException(String message) {
        super(message);
    }

    /**
     * Constructs a new MessageProcessingException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public MessageProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
