package com.euonia.bus;

import com.euonia.bus.convention.MessageConvention;
import com.euonia.bus.strategy.TransportStrategy;

import java.util.List;

public interface MessageBusOptions {
    /**
     * Gets the name of the default transport that will be used when no specific transport is assigned to a message type by strategy.
     *
     * @return The name of the default transport.
     */
    String getDefaultTransport();

    /**
     * Indicates whether pipeline behaviors are enabled for the message bus.
     *
     * @return true if pipeline behaviors are enabled, false otherwise.
     */
    boolean isEnablePipelineBehaviors();

    /**
     * Gets the message convention used by the message bus.
     *
     * @return The message convention used by the message bus.
     */
    MessageConvention getConvention();

    /**
     * Gets the list of types for which a transport strategy has been assigned.
     *
     * @return The list of types for which a transport strategy has been assigned.
     */
    List<String> getStrategyAssignedTypes();

    /**
     * Gets the transport strategy for the specified transport name.
     *
     * @param transport The name of the transport.
     * @return The transport strategy associated with the specified transport name.
     */
    TransportStrategy getStrategy(String transport);
}
