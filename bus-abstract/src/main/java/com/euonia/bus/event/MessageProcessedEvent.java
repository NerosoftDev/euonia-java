package com.euonia.bus.event;

import com.euonia.bus.MessageContext;

public class MessageProcessedEvent {
    private final Object message;
    private final MessageContext context;
    private final MessageProcessType processType;

    public MessageProcessedEvent(Object message, MessageContext context, MessageProcessType processType) {
        this.message = message;
        this.context = context;
        this.processType = processType;
    }

    public Object getMessage() {
        return message;
    }

    public MessageContext getContext() {
        return context;
    }

    public MessageProcessType getProcessType() {
        return processType;
    }
}
