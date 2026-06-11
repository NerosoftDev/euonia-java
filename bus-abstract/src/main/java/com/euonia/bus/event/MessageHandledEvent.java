package com.euonia.bus.event;

public class MessageHandledEvent {
    private final Object message;
    private Class<?> handlerType;

    public MessageHandledEvent(Object message) {
        this.message = message;
    }

    public Class<?> getHandlerType() {
        return handlerType;
    }

    public void setHandlerType(Class<?> handlerType) {
        this.handlerType = handlerType;
    }

    public Object getMessage() {
        return message;
    }
}
