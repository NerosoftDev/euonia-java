package com.euonia.bus;

import com.euonia.bus.convention.MessageConventionBuilder;
import com.euonia.bus.strategy.TransportStrategyBuilder;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * BusConfigurator is the main interface for configuring the message bus.
 * It provides methods to configure message conventions, transport strategies, and handler registrations.
 * Implementations of this interface will be used to set up the bus before it is started.
 */
public interface BusConfigurator {
    /**
     * Gets the MessageConventionBuilder that has been configured.
     *
     * @return the MessageConventionBuilder
     */
    MessageConventionBuilder getConventionBuilder();

    /**
     * Gets the map of transport strategy builders that have been configured.
     *
     * @return the map of transport strategy builders
     */
    ConcurrentMap<String, TransportStrategyBuilder> getStrategyBuilders();

    /**
     * Gets the list of handler registrations that have been configured.
     *
     * @return the list of handler registrations
     */
    List<HandlerRegistration> getRegistrations();
}
