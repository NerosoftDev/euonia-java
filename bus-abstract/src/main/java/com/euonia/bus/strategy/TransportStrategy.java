package com.euonia.bus.strategy;

/**
 * Defines the contract for a transport strategy, which determines how messages are handled for outgoing and incoming operations.
 */
public interface TransportStrategy {
    /**
     * Gets the name of the transport strategy.
     *
     * @return the name of the transport strategy
     */
    String getName();

    /**
     * Determines if the transport strategy can handle outgoing messages of the specified type.
     *
     * @param messageType the type of the message
     * @return true if the transport strategy can handle outgoing messages of the specified type, false otherwise
     */
    boolean outgoing(Class<?> messageType);

    /**
     * Determines if the transport strategy can handle incoming messages of the specified type.
     *
     * @param messageType the type of the message
     * @return true if the transport strategy can handle incoming messages of the specified type, false otherwise
     */
    boolean incoming(Class<?> messageType);
}
