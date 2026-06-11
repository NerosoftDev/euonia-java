package com.euonia.bus.strategy;

import java.util.function.Predicate;

class OverridableTransportStrategy implements TransportStrategy {

    private final TransportStrategy delegate;
    private Predicate<Class<?>> outgoingPredicate;
    private Predicate<Class<?>> incomingPredicate;

    OverridableTransportStrategy(TransportStrategy delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return String.format("Override with (%s)", delegate.getName());
    }

    @Override
    public boolean outgoing(Class<?> messageType) {
        if (outgoingPredicate == null) {
            return delegate.outgoing(messageType);
        } else {
            return outgoingPredicate.test(messageType);
        }
    }

    @Override
    public boolean incoming(Class<?> messageType) {
        if (incomingPredicate == null) {
            return delegate.incoming(messageType);
        } else {
            return incomingPredicate.test(messageType);
        }
    }

    void setOutgoingPredicate(Predicate<Class<?>> outgoingPredicate) {
        this.outgoingPredicate = outgoingPredicate;
    }

    void setIncomingPredicate(Predicate<Class<?>> incomingPredicate) {
        this.incomingPredicate = incomingPredicate;
    }
}
