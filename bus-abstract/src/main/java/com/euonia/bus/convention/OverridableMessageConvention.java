package com.euonia.bus.convention;

import java.util.function.Predicate;

class OverridableMessageConvention implements MessageConvention {
    private final MessageConvention innerConvention;
    private Predicate<Class<?>> unicastPredicate;
    private Predicate<Class<?>> multicastPredicate;
    private Predicate<Class<?>> requestPredicate;

    OverridableMessageConvention(MessageConvention innerConvention) {
        this.innerConvention = innerConvention;
    }

    @Override
    public String getName() {
        return String.format("Override with %s", innerConvention.getName());
    }

    @Override
    public boolean isUnicastType(Class<?> messageType) {
        if (unicastPredicate == null) {
            return innerConvention.isUnicastType(messageType);
        } else {
            return unicastPredicate.test(messageType);
        }
    }

    @Override
    public boolean isMulticastType(Class<?> messageType) {
        if (multicastPredicate == null) {
            return innerConvention.isMulticastType(messageType);
        } else {
            return multicastPredicate.test(messageType);
        }
    }

    @Override
    public boolean isRequestType(Class<?> messageType) {
        if (requestPredicate == null) {
            return innerConvention.isRequestType(messageType);
        } else {
            return requestPredicate.test(messageType);
        }
    }

    public void setUnicastPredicate(Predicate<Class<?>> unicastPredicate) {
        this.unicastPredicate = unicastPredicate;
    }

    public void setMulticastPredicate(Predicate<Class<?>> multicastPredicate) {
        this.multicastPredicate = multicastPredicate;
    }

    public void setRequestPredicate(Predicate<Class<?>> requestPredicate) {
        this.requestPredicate = requestPredicate;
    }
}
