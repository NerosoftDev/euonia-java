package com.euonia.bus;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.euonia.bus.event.MessageSubscribedEvent;

/**
 * Specifies contract of message handler context.
 *
 * @see MessageContext
 * @see MessageSubscribedEvent
 */
public interface HandlerContext {

    /**
     * Adds a listener for message subscribed events.
     *
     * @param listener the consumer to invoke when a message is subscribed
     */
    void onMessageSubscribed(Consumer<MessageSubscribedEvent> listener);

    /**
     * Handle message asynchronously.
     *
     * @param message the message to be handled
     * @param context the message context
     * @return a {@link CompletableFuture} representing the pending completion of the operation
     */
    CompletableFuture<Void> handleAsync(Object message, MessageContext context);

    /**
     * Handle message asynchronously with the specified channel.
     *
     * @param channel the channel name
     * @param message the message to be handled
     * @param context the message context
     * @return a {@link CompletableFuture} representing the pending completion of the operation
     */
    CompletableFuture<Void> handleAsync(String channel, Object message, MessageContext context);
}
