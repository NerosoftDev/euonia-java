package com.euonia.bus.message;

import com.euonia.reflection.ServiceProvider;

@FunctionalInterface
public interface MessageHandlerFactory {
    /**
     * Create a new MessageHandler instance.
     *
     * @return a new MessageHandler instance
     */
    MessageHandler createHandler(ServiceProvider provider);
}
