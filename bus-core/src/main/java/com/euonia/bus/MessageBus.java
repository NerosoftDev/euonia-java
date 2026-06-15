package com.euonia.bus;

import com.euonia.bus.contract.Request;
import com.euonia.bus.contract.Transport;
import com.euonia.bus.exception.MessageTransportException;
import com.euonia.bus.exception.MessageTypeException;
import com.euonia.bus.message.MessageCache;
import com.euonia.bus.message.PipelineMessage;
import com.euonia.bus.options.CallOptions;
import com.euonia.bus.options.MessageBusOptions;
import com.euonia.bus.options.PublishOptions;
import com.euonia.bus.options.SendOptions;
import com.euonia.core.GuidType;
import com.euonia.core.ObjectId;
import com.euonia.http.RequestContextAccessor;
import com.euonia.pipeline.PipelineFactory;
import com.euonia.reflection.ServiceProvider;
import com.euonia.utility.StringUtility;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MessageBus implements Bus {
    private static final Logger LOGGER = Logger.getLogger(MessageBus.class.getName());

    private final Dispatcher dispatcher;
    private final ServiceProvider provider;
    private final MessageBusOptions options;
    private final RequestContextAccessor requestAccessor;
    private final PipelineFactory pipelineFactory;

    public MessageBus(ServiceProvider provider) {
        this.provider = provider;
        this.dispatcher = provider.getService(Dispatcher.class).orElseThrow(() -> new IllegalStateException("Dispatcher service not found"));
        this.options = provider.getService(MessageBusOptions.class).orElse(null);
        this.requestAccessor = provider.getService(RequestContextAccessor.class).orElse(null);
        this.pipelineFactory = provider.getService(PipelineFactory.class).orElse(null);
    }

    public MessageBus(ServiceProvider provider, Dispatcher dispatcher, MessageBusOptions options) {
        this(provider, dispatcher, options, provider.getService(RequestContextAccessor.class).orElse(null));
    }

    public MessageBus(ServiceProvider provider, Dispatcher dispatcher, MessageBusOptions options, RequestContextAccessor requestAccessor) {
        this.dispatcher = dispatcher;
        this.provider = provider;
        this.options = options;
        this.requestAccessor = requestAccessor;
        this.pipelineFactory = provider.getService(PipelineFactory.class).orElse(null);
    }

    @Override
    public <T> CompletableFuture<Void> publishAsync(T message, Consumer<PipelineMessage<RoutedMessage<T>, Void>> behavior, PublishOptions publishOptions, Consumer<MessageMetadata> metadataSetter) {
        LOGGER.fine("publishAsync called for message: " + message.getClass().getName());
        if (publishOptions == null) {
            publishOptions = new PublishOptions();
        }

        var messageType = message.getClass();
        if (options.getConvention().isMulticastType(messageType)) {
            throw new MessageTypeException("The message type " + message.getClass().getName() + " is not multicast");
        }

        var context = requestAccessor.getContext();

        var channelName = StringUtility.collapse(
            publishOptions::getChannel,
            () -> MessageCache.getInstance().getOrAddChannel(messageType)
        );

        RoutedMessage<T> pack = new RoutedMessage<T>(message, channelName);
        pack.setMessageId(StringUtility.collapse(publishOptions::getMessageId, () -> ObjectId.newGuid(GuidType.SEQUENTIAL_AS_STRING).toString()));
        pack.setRequestTrackId(StringUtility.collapse(context::getTraceIdentifier, context::getRequestId, () -> ObjectId.newGuid(GuidType.SEQUENTIAL_AS_STRING).toString()));
        pack.setAuthorization(context.getAuthorization());

        if (metadataSetter != null) {
            metadataSetter.accept(pack.getMetadata());
        }

        if (publishOptions.isEnablePipelineBehaviors() == true || options.isEnablePipelineBehaviors()) {
            var pipeline = pipelineFactory.<RoutedMessage<T>, Void>create();
            if (pipeline != null) {
                var pipelineMessage = new PipelineMessage<>(pack, pipeline);
                if (publishOptions.isAttachDefaultPipelineBehaviors()) {
                    pipelineMessage.getPipeline().useOf(messageType, true);
                }

                if (behavior != null) {
                    behavior.accept(pipelineMessage);
                }
                pipelineMessage.executeAsync()
                               .exceptionally(ex -> {
                                   LOGGER.log(Level.SEVERE, "Pipeline execution failed for message: " + messageType.getName(), ex);
                                   return null;
                               }).join();
                pack = pipelineMessage.getMessage();
            }


        }

        var transports = dispatcher.determine(messageType);

        RoutedMessage<T> finalPack = pack;
        var tasks = transports.stream()
                              .map(transport -> {
                                  LOGGER.log(Level.INFO, String.format("[%s] Publishing message of type %s to transport %s on channel %s.", finalPack.getMessageId(), messageType.getName(), transport, channelName));
                                  return provider.getService(Transport.class, transport)
                                                 .orElseThrow(() -> new MessageTransportException("Transport service not found: " + transport))
                                                 .sendAsync(finalPack);
                              })
                              .toArray(CompletableFuture[]::new);

        return CompletableFuture.allOf(tasks);
    }

    @Override
    public <T, R> CompletableFuture<Void> sendAsync(T message, Class<R> responseType, Flow.Subscriber<R> callback, Consumer<PipelineMessage<RoutedMessage<T>, R>> behavior, SendOptions sendOptions, Consumer<MessageMetadata> metadataSetter) {
        if (sendOptions == null) {
            sendOptions = new SendOptions();
        }

        var messageType = message.getClass();

        if (options.getConvention().isUnicastType(messageType)) {
            throw new MessageTypeException("The message type " + message.getClass().getName() + " is not unicast");
        }

        var context = requestAccessor.getContext();

        var channelName = StringUtility.collapse(
            sendOptions::getChannel,
            () -> MessageCache.getInstance().getOrAddChannel(messageType)
        );

        RoutedMessage<T> pack = new RoutedMessage<T>(message, channelName);
        pack.setMessageId(StringUtility.collapse(sendOptions::getMessageId, () -> ObjectId.newGuid(GuidType.SEQUENTIAL_AS_STRING).toString()));
        pack.setRequestTrackId(StringUtility.collapse(context::getTraceIdentifier, context::getRequestId, () -> ObjectId.newGuid(GuidType.SEQUENTIAL_AS_STRING).toString()));
        pack.setAuthorization(context.getAuthorization());
        pack.setCorrelationId(StringUtility.collapse(sendOptions::getCorrelationId, () -> ObjectId.newGuid(GuidType.SEQUENTIAL_AS_STRING).toString()));

        if (metadataSetter != null) {
            metadataSetter.accept(pack.getMetadata());
        }

        if (sendOptions.isEnablePipelineBehaviors() == true || options.isEnablePipelineBehaviors()) {
//            var pipeline = provider.getService(Pipeline.class)
//                                   .orElse(null);
            var pipeline = pipelineFactory.<RoutedMessage<T>, R>create();
            if (pipeline != null) {
                var pipelineMessage = new PipelineMessage<>(pack, pipeline);
                if (sendOptions.isAttachDefaultPipelineBehaviors()) {
                    pipelineMessage.getPipeline().useOf(messageType, true);
                }

                if (behavior != null) {
                    behavior.accept(pipelineMessage);
                }
                pipelineMessage.executeAsync()
                               .exceptionally(ex -> {
                                   LOGGER.log(Level.SEVERE, "Pipeline execution failed for message: " + messageType.getName(), ex);
                                   return null;
                               }).join();
                pack = pipelineMessage.getMessage();
            }
        }

        var transportName = dispatcher.determine(messageType).get(0);
        var transport = provider.getService(Transport.class, transportName)
                                .orElseThrow(() -> new MessageTransportException("The transport " + transportName + " was not found."));
        RoutedMessage<T> finalPack = pack;
        return transport.sendAsync(finalPack, responseType)
                        .whenComplete((response, ex) -> {
                            if (ex != null) {
                                LOGGER.log(Level.SEVERE, "Failed to send message: " + messageType.getName(), ex);
                                if (callback != null) {
                                    callback.onError(ex);
                                }
                            } else {
                                callback.onNext(response);
                            }
                        })
                        .thenAccept(v -> {
                            if (callback != null) {
                                callback.onComplete();
                            }
                        });
    }

    @Override
    public <T extends Request<R>, R> CompletableFuture<R> callAsync(T request, Class<R> responseType, Consumer<PipelineMessage<RoutedMessage<T>, R>> behavior, CallOptions callOptions, Consumer<MessageMetadata> metadataSetter) {
        if (callOptions == null) {
            callOptions = new CallOptions();
        }

        var messageType = request.getClass();

        if (options.getConvention().isRequestType(messageType)) {
            throw new MessageTypeException("The message type " + messageType.getName() + " is not unicast");
        }

        var context = requestAccessor.getContext();

        var channelName = StringUtility.collapse(
            callOptions::getChannel,
            () -> MessageCache.getInstance().getOrAddChannel(messageType)
        );

        RoutedMessage<T> pack = new RoutedMessage<T>(request, channelName);
        pack.setMessageId(StringUtility.collapse(callOptions::getMessageId, () -> ObjectId.newGuid(GuidType.SEQUENTIAL_AS_STRING).toString()));
        pack.setRequestTrackId(StringUtility.collapse(context::getTraceIdentifier, context::getRequestId, () -> ObjectId.newGuid(GuidType.SEQUENTIAL_AS_STRING).toString()));
        pack.setAuthorization(context.getAuthorization());
        pack.setCorrelationId(StringUtility.collapse(callOptions::getCorrelationId, () -> ObjectId.newGuid(GuidType.SEQUENTIAL_AS_STRING).toString()));

        if (metadataSetter != null) {
            metadataSetter.accept(pack.getMetadata());
        }

        if (callOptions.isEnablePipelineBehaviors() == true || options.isEnablePipelineBehaviors()) {
            var pipeline = pipelineFactory.<RoutedMessage<T>, R>create();
            if (pipeline != null) {
                var pipelineMessage = new PipelineMessage<>(pack, pipeline);
                if (callOptions.isAttachDefaultPipelineBehaviors()) {
                    pipelineMessage.getPipeline().useOf(messageType, true);
                }

                if (behavior != null) {
                    behavior.accept(pipelineMessage);
                }
                pipelineMessage.executeAsync()
                               .exceptionally(ex -> {
                                   LOGGER.log(Level.SEVERE, "Pipeline execution failed for message: " + messageType.getName(), ex);
                                   return null;
                               }).join();
                pack = pipelineMessage.getMessage();
            }
        }

        var transportName = dispatcher.determine(messageType).get(0);
        var transport = provider.getService(Transport.class, transportName)
                                .orElseThrow(() -> new MessageTransportException("The transport " + transportName + " was not found."));

        return transport.requestAsync(pack);
    }
}
