package com.euonia.bus.event;

import com.euonia.bus.MessageContext;

public class MessageAcknowledgedEvent extends MessageProcessedEvent {
    public MessageAcknowledgedEvent(Object message, MessageContext context) {
        super(message, context, MessageProcessType.RECEIVED);
    }
}
