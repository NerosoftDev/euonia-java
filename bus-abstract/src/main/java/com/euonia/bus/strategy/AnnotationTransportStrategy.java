package com.euonia.bus.strategy;

import com.euonia.bus.annotation.DispatchIn;
import com.euonia.bus.annotation.ReceiveIn;

public class AnnotationTransportStrategy implements TransportStrategy {
    private final String[] required;

    public AnnotationTransportStrategy(String... required) {
        this.required = required;
    }

    @Override
    public String getName() {
        return "AnnotationTransportStrategy";
    }

    @Override
    public boolean outgoing(Class<?> messageType) {
        var annotation = messageType.getAnnotation(DispatchIn.class);
        return annotation != null && annotation.transports().length > 0 && containsRequired(annotation.transports());
    }

    @Override
    public boolean incoming(Class<?> messageType) {
        var annotation = messageType.getAnnotation(ReceiveIn.class);
        return annotation != null && annotation.transports().length > 0 && containsRequired(annotation.transports());
    }

    private boolean containsRequired(String[] values) {
        for (String value : values) {
            for (String req : required) {
                if (value.equals(req)) {
                    return true;
                }
            }
        }
        return false;
    }
}
