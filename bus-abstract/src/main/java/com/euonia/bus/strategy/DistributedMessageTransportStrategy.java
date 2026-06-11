package com.euonia.bus.strategy;

import com.euonia.bus.annotation.DistributedMessage;

public class DistributedMessageTransportStrategy implements TransportStrategy {

    @Override
    public String getName() {
        return "DistributedMessageTransportStrategy";
    }

    @Override
    public boolean outgoing(Class<?> messageType) {
        return messageType.getAnnotation(DistributedMessage.class) != null;
    }

    @Override
    public boolean incoming(Class<?> messageType) {
        return messageType.getAnnotation(DistributedMessage.class) != null;
    }
}
