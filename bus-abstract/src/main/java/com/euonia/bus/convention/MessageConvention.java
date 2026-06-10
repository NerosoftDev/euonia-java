package com.euonia.bus.convention;

/**
 * A set of conventions for determining if a class represents a request, multicast, or unicast message.
 */
public interface MessageConvention {
    /**
     * Gets the name of the convention. Used for diagnostic purposes.
     *
     * @return the name of the convention
     */
    String getName();

    /**
     * Determines if the given message type is a unicast message. A unicast message is a message that is sent to a single recipient.
     *
     * @param messageType the type of the message
     * @return true if the message type is a unicast message, false otherwise
     */
    boolean isUnicastType(Class<?> messageType);

    /**
     * Determines if the given message type is a multicast message. A multicast message is a message that is sent to multiple recipients.
     *
     * @param messageType the type of the message
     * @return true if the message type is a multicast message, false otherwise
     */
    boolean isMulticastType(Class<?> messageType);

    /**
     * Determines if the given message type is a request message. A request message is a message that expects a response.
     *
     * @param messageType the type of the message
     * @return true if the message type is a request message, false otherwise
     */
    boolean isRequestType(Class<?> messageType);
}
