package com.euonia.bus;

/**
 * MessageEnvelope defines the metadata for messages sent through the bus.
 */
public interface MessageEnvelope {
    String getMessageId();

    String getCorrelationId();

    String getConversationId();

    String getRequestTrackId();

    String getChannel();
}
