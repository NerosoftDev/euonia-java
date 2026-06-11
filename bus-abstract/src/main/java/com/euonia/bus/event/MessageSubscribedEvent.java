package com.euonia.bus.event;

/**
 * Represents the arguments for a message subscribed event.
 */
public class MessageSubscribedEvent {

    private final String channel;
    private final Class<?> messageType;
    private final Class<?> handlerType;

    public MessageSubscribedEvent(String channel, Class<?> messageType, Class<?> handlerType) {
        this.channel = channel;
        this.messageType = messageType;
        this.handlerType = handlerType;
    }

    public String getChannel() {
        return channel;
    }

    public Class<?> getMessageType() {
        return messageType;
    }

    public Class<?> getHandlerType() {
        return handlerType;
    }
}
