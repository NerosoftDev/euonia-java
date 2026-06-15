package com.euonia.bus;

import com.euonia.bus.contract.Request;
import com.euonia.bus.message.PipelineMessage;
import com.euonia.bus.options.CallOptions;
import com.euonia.bus.options.PublishOptions;
import com.euonia.bus.options.SendOptions;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.function.Consumer;

public interface Bus {
    default <T> CompletableFuture<Void> publishAsync(T message) {
        return publishAsync(message, null);
    }

    default <T> CompletableFuture<Void> publishAsync(T message, Consumer<PipelineMessage<RoutedMessage<T>, Void>> behavior) {
        return publishAsync(message, behavior, new PublishOptions());
    }

    default <T> CompletableFuture<Void> publishAsync(T message, Consumer<PipelineMessage<RoutedMessage<T>, Void>> behavior, PublishOptions options) {
        return publishAsync(message, behavior, options, null);
    }

    <T> CompletableFuture<Void> publishAsync(T message, Consumer<PipelineMessage<RoutedMessage<T>, Void>> behavior, PublishOptions options, Consumer<MessageMetadata> metadataSetter);

    default <T> CompletableFuture<Void> publishAsync(String channel, T message, Consumer<PipelineMessage<RoutedMessage<T>, Void>> behavior, Consumer<MessageMetadata> metadataSetter) {
        var options = new PublishOptions();
        options.setChannel(channel);
        return publishAsync(message, behavior, options, metadataSetter);
    }

    default <T> CompletableFuture<Void> sendAsync(T message) {
        return sendAsync(message, null);
    }

    default <T> CompletableFuture<Void> sendAsync(T message, Consumer<PipelineMessage<RoutedMessage<T>, Void>> behavior) {
        return sendAsync(message, behavior, new SendOptions());
    }

    default <T> CompletableFuture<Void> sendAsync(T message, Consumer<PipelineMessage<RoutedMessage<T>, Void>> behavior, SendOptions sendOptions) {
        return sendAsync(message, behavior, sendOptions, null);
    }

    default <T> CompletableFuture<Void> sendAsync(T message, Consumer<PipelineMessage<RoutedMessage<T>, Void>> behavior, SendOptions sendOptions, Consumer<MessageMetadata> metadataSetter) {
        return sendAsync(message, Void.class, null, behavior, sendOptions, metadataSetter);
    }

    default <T, R> CompletableFuture<Void> sendAsync(T message, Class<R> responseType, Flow.Subscriber<R> callback) {
        return sendAsync(message, responseType, callback, null);
    }

    default <T, R> CompletableFuture<Void> sendAsync(T message, Class<R> responseType, Flow.Subscriber<R> callback, Consumer<PipelineMessage<RoutedMessage<T>, R>> behavior) {
        return sendAsync(message, responseType, callback, behavior, new SendOptions());
    }

    default <T, R> CompletableFuture<Void> sendAsync(T message, Class<R> responseType, Flow.Subscriber<R> callback, Consumer<PipelineMessage<RoutedMessage<T>, R>> behavior, SendOptions sendOptions) {
        return sendAsync(message, responseType, callback, behavior, sendOptions, null);
    }

    <T, R> CompletableFuture<Void> sendAsync(T message, Class<R> responseType, Flow.Subscriber<R> callback, Consumer<PipelineMessage<RoutedMessage<T>, R>> behavior, SendOptions sendOptions, Consumer<MessageMetadata> metadataSetter);

    default <T extends Request<R>, R> CompletableFuture<R> callAsync(T request, Class<R> responseType) {
        return callAsync(request, responseType, null);
    }

    default <T extends Request<R>, R> CompletableFuture<R> callAsync(T request, Class<R> responseType, Consumer<PipelineMessage<RoutedMessage<T>, R>> behavior) {
        return callAsync(request, responseType, behavior, new CallOptions());
    }

    default <T extends Request<R>, R> CompletableFuture<R> callAsync(T request, Class<R> responseType, Consumer<PipelineMessage<RoutedMessage<T>, R>> behavior, CallOptions callOptions) {
        return callAsync(request, responseType, behavior, callOptions, null);
    }

    <T extends Request<R>, R> CompletableFuture<R> callAsync(T request, Class<R> responseType, Consumer<PipelineMessage<RoutedMessage<T>, R>> behavior, CallOptions callOptions, Consumer<MessageMetadata> metadataSetter);
}
