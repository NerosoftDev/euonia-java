package com.euonia.bus.convention;

import com.euonia.bus.annotation.Unicast;
import com.euonia.bus.annotation.Multicast;
import com.euonia.bus.annotation.Request;

/**
 * Evaluate whether a type is a message, command, event, or request by annotation decorated on the type.
 */
public class AnnotationMessageConvention implements MessageConvention {
    private static final String NAME = "Annotation decoration message convention";
    private static final String MESSAGE_TYPE_NULL_ERROR = "messageType cannot be null.";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isUnicastType(Class<?> messageType) {
        assert messageType != null : MESSAGE_TYPE_NULL_ERROR;
        return messageType.getAnnotation(Unicast.class) != null;
    }

    @Override
    public boolean isMulticastType(Class<?> messageType) {
        assert messageType != null : MESSAGE_TYPE_NULL_ERROR;
        return messageType.getAnnotation(Multicast.class) != null;
    }

    @Override
    public boolean isRequestType(Class<?> messageType) {
        assert messageType != null : MESSAGE_TYPE_NULL_ERROR;
        return messageType.getAnnotation(Request.class) != null;
    }
}
