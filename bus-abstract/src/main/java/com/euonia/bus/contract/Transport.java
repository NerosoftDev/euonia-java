package com.euonia.bus.contract;

import com.euonia.bus.RoutedMessage;

import java.util.concurrent.CompletableFuture;

/**
 * Transport is responsible for sending messages to other nodes in the system. It provides methods for publishing events, sending commands, and making request-response calls.
 * Each transport implementation can use different underlying communication protocols (e.g., HTTP, gRPC, WebSocket, etc.) to send messages.
 * The transport is also responsible for handling message serialization and deserialization, as well as managing connections and ensuring message delivery.
 * The Transport interface defines the contract for sending messages, and different implementations can be created to support various communication protocols and patterns.
 */
public interface Transport {
    /**
     * Gets the name of the transport. This can be used to identify the transport implementation being used (e.g., "HTTP", "gRPC", "WebSocket", etc.).
     *
     * @return the name of the transport
     */
    String getName();

    /**
     * Publishes a message to the transport.
     * This method is typically used for sending events or messages that do not expect a response.
     * The message is sent asynchronously, and the method returns a CompletableFuture that completes when the message has been successfully published.
     *
     * @param message the message to be published
     * @param <M>     the type of the message
     * @return a CompletableFuture that completes when the message has been successfully published
     */
    <M> CompletableFuture<Void> publishAsync(RoutedMessage<M> message);

    /**
     * Sends a message to a specific destination. This method is typically used for sending commands or messages that expect a response.
     * The message is sent asynchronously, and the method returns a CompletableFuture that completes when the message has been successfully sent.
     *
     * @param message the message to be sent
     * @param <M>     the type of the message
     * @return a CompletableFuture that completes when the message has been successfully sent
     */
    <M> CompletableFuture<Void> sendAsync(RoutedMessage<M> message);

    /**
     * Sends a request message and expects a response. This method is typically used for request-response communication patterns.
     * The message is sent asynchronously, and the method returns a CompletableFuture that completes with the response when it is received.
     *
     * @param message the request message to be sent
     * @param <M>     the type of the request message
     * @param <R>     the type of the response message
     * @return a CompletableFuture that completes with the response when it is received
     */
    <M, R> CompletableFuture<R> requestAsync(RoutedMessage<M> message);
}
