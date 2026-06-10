package com.euonia.bus.strategy;

import com.euonia.bus.annotation.LocalMessage;

public class LocalMessageTransportStrategy implements TransportStrategy {

    @Override
    public String getName() {
        return "LocalMessageTransportStrategy";
    }

    @Override
    public boolean outgoing(Class<?> messageType) {
        return messageType.getAnnotation(LocalMessage.class) != null;
    }

    @Override
    public boolean incoming(Class<?> messageType) {
        return messageType.getAnnotation(LocalMessage.class) != null;
    }
}
