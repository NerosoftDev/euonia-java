package com.euonia.bus.message;

import com.euonia.bus.MessageContext;

@FunctionalInterface
public interface MessageHandler {
    Object handle(Object message, MessageContext context) throws Exception;
}
