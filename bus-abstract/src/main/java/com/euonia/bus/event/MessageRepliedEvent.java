package com.euonia.bus.event;

/**
 * Represents the arguments for a message replied event.
 */
public class MessageRepliedEvent {
    private final Object result;

    /**
     * Initializes a new instance of the MessageRepliedEventArgs class with the specified result.
     *
     * @param result the result of the message reply
     */
    public MessageRepliedEvent(Object result) {
        this.result = result;
    }

    /**
     * Gets the result of the message reply.
     *
     * @return the result of the message reply
     */
    public Object getResult() {
        return result;
    }
}
