package com.euonia.bus.event;

import com.euonia.bus.MessageContext;

public class MessageDeliveredEvent extends MessageProcessedEvent {
    public MessageDeliveredEvent(Object message, MessageContext context) {
        super(message, context, MessageProcessType.DELIVERED);
    }
}
