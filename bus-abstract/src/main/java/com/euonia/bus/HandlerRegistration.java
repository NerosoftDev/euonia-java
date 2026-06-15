package com.euonia.bus;

import java.lang.reflect.Method;

/**
 * HandlerRegistration represents the registration of a message handler for a
 * specific channel and message type.
 */
public record HandlerRegistration(String channel, Class<?> messageType, Class<?> handlerType, Method method) {
}
