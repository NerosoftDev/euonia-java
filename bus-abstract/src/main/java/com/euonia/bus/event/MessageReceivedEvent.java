package com.euonia.bus.event;

import com.euonia.bus.MessageContext;

public class MessageReceivedEvent extends MessageProcessedEvent {
    public MessageReceivedEvent(Object message, MessageContext context) {
        super(message, context, MessageProcessType.RECEIVED);
    }
}
