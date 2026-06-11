package com.euonia.bus.exception;

/**
 * Exception thrown when an error occurs during message delivery.
 */
public class MessageDeliverException extends RuntimeException {

    /**
     * Constructs a new MessageDeliverException with a default error message.
     */
    public MessageDeliverException() {
        this("Error occurred during message deliver.");
    }

    /**
     * Constructs a new MessageDeliverException with the specified error message.
     *
     * @param message the detail message
     */
    public MessageDeliverException(String message) {
        super(message);
    }
}
