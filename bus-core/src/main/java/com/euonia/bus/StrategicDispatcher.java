package com.euonia.bus;

import com.euonia.bus.exception.MessageTypeException;
import com.euonia.bus.options.MessageBusOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A dispatcher that determines the transport(s) for a message type based on configured strategies and conventions.
 * It uses a cache to store the transport determinations for message types to improve performance.
 */
public class StrategicDispatcher implements Dispatcher {
    private final ConcurrentHashMap<Class<?>, List<String>> transportCache = new ConcurrentHashMap<>();
    private final MessageBusOptions options;

    /**
     * Creates a new instance of StrategicDispatcher with the specified options.
     *
     * @param options the message bus options
     */
    public StrategicDispatcher(MessageBusOptions options) {
        this.options = options;
    }

    /**
     * Creates the transports for the specified message type.
     *
     * @param messageType the type of the message
     * @return a list of transport names to which the message should be dispatched
     */
    @Override
    public List<String> determine(Class<?> messageType) {
        var transportTypes = transportCache.computeIfAbsent(messageType, t -> {
            var list = new ArrayList<String>();
            for (var type : options.getStrategyAssignedTypes()) {
                var strategy = options.getStrategy(type);
                if (strategy.outgoing(messageType)) {
                    list.add(type);
                }
            }
            return list;
        });

        switch (transportTypes.size()) {
            case 0:
                if (options.getDefaultTransport() == null || options.getDefaultTransport().isEmpty()) {
                    throw new MessageTypeException("No transport is configured for the message type. Message type: " + messageType.getName());
                }
                transportTypes.add(options.getDefaultTransport());
                break;
            case 1:
                break;
            default:
                if (!options.getConvention().isMulticastType(messageType)) {
                    throw new MessageTypeException("The message type is not identified as a multicast type, but multiple transport strategies are configured for it. Message type: " + messageType.getName());
                }
                break;
        }

        return transportTypes;
    }
}
