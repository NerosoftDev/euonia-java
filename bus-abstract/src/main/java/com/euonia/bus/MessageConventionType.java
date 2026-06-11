package com.euonia.bus;

/**
 * Defines the convention type for messages in the bus system.
 * This enum is used to specify how messages are routed and handled within the bus, allowing for different communication patterns such as unicast, multicast, and request-response.
 */
public enum MessageConventionType {
    /**
     * Indicates that no specific message convention is being used. This can be used as a default value when no convention is applicable or when the convention is determined dynamically at runtime.
     */
    NONE,
    /**
     * Indicates that the message is intended for a single recipient.
     */
    UNICAST,
    /**
     * Indicates that the message is intended for multiple recipients.
     */
    MULTICAST,
    /**
     * Indicates that the message follows a request-response pattern.
     */
    REQUEST
}
