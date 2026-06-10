package com.euonia.bus;

import java.lang.reflect.Method;

/**
 * MessageRegistration represents the registration of a message handler for a
 * specific channel and message type.
 */
public class MessageRegistration {
    private final String channel;
    private final Class<?> messageType;
    private final Class<?> handlerType;
    private final Method method;

    /**
     * Constructs a new MessageRegistration with the specified channel, message
     * type, handler type, and method.
     *
     * @param channel     the channel for the message
     * @param messageType the type of the message
     * @param handlerType the type of the handler
     * @param method      the method to handle the message
     */
    public MessageRegistration(String channel, Class<?> messageType, Class<?> handlerType, Method method) {
        this.channel = channel;
        this.messageType = messageType;
        this.handlerType = handlerType;
        this.method = method;
    }

    /**
     * Gets the channel for the message.
     *
     * @return the channel for the message
     */
    public String getChannel() {
        return channel;
    }

    /**
     * Gets the type of the message.
     *
     * @return the type of the message
     */
    public Class<?> getMessageType() {
        return messageType;
    }

    /**
     * Gets the type of the handler.
     *
     * @return the type of the handler
     */
    public Class<?> getHandlerType() {
        return handlerType;
    }

    /**
     * Gets the method to handle the message.
     *
     * @return the method to handle the message
     */
    public Method getMethod() {
        return method;
    }
}
