package com.euonia.bus;

import java.util.List;

/**
 * Defines a dispatcher that determines handler types for messages.
 */
public interface Dispatcher {
    /**
     * Determine the transport(s) to which the message of the given type should be dispatched.
     *
     * @param messageType the type of the message
     * @return a list of transport names
     */
    List<String> determine(Class<?> messageType);
}
