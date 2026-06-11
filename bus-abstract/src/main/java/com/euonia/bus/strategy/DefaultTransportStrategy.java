package com.euonia.bus.strategy;

public class DefaultTransportStrategy implements TransportStrategy {

    @Override
    public String getName() {
        return "DefaultTransportStrategy";
    }

    @Override
    public boolean outgoing(Class<?> messageType) {
        return false;
    }

    @Override
    public boolean incoming(Class<?> messageType) {
        return false;
    }
}
