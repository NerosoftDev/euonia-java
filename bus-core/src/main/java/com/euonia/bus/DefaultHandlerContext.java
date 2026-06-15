package com.euonia.bus;

import java.lang.reflect.InvocationTargetException;

import com.euonia.bus.convention.MessageConvention;
import com.euonia.bus.convention.BaseMessageConvention;
import com.euonia.bus.event.MessageSubscribedEvent;
import com.euonia.bus.message.MessageCache;
import com.euonia.bus.message.MessageHandlerFactory;
import com.euonia.http.InternalServerErrorException;
import com.euonia.reflection.ServiceProvider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default message handler context using the Euonia {@link ServiceProvider} for dependency resolution.
 * <p>
 * This class manages handler registrations and dispatches incoming messages to the appropriate
 * handlers. It supports both generic handler type registration (resolved via DI) and
 * reflection-based registration via {@link HandlerRegistration}.
 */
final class DefaultHandlerContext implements HandlerContext {

    private static final Logger LOGGER = Logger.getLogger(DefaultHandlerContext.class.getName());

    private final ConcurrentMap<String, List<MessageHandlerFactory>> handlerContainer = new ConcurrentHashMap<>();
    private final SubmissionPublisher<MessageSubscribedEvent> publisher = new SubmissionPublisher<>();
    private final ServiceProvider provider;
    private final MessageConvention convention;

    /**
     * Initializes a new instance of {@link DefaultHandlerContext}.
     *
     * @param provider the service provider used to resolve handlers and other services
     */
    public DefaultHandlerContext(ServiceProvider provider) {
        this.provider = provider;
        this.convention = this.provider.getService(MessageConvention.class)
                                       .orElse(new BaseMessageConvention());
    }

    // region Event subscription

    @Override
    public void onMessageSubscribed(Consumer<MessageSubscribedEvent> listener) {
        publisher.consume(listener);
    }

    // endregion

    // region Handler registration

    /**
     * Registers a message handler type for the given message type.
     * The handler will be resolved from the {@link ServiceProvider} at dispatch time.
     *
     * @param <M>         the message type
     * @param <R>         the response type
     * @param <H>         the handler type implementing {@link Handler}{@code <M, R>}
     * @param messageType the class of the message
     * @param handlerType the class of the handler
     */
    <M, R, H extends Handler<M, R>> void register(Class<M> messageType, Class<H> handlerType) {
        var channel = MessageCache.getInstance().getOrAddChannel(messageType);

        MessageHandlerFactory factory = sp -> {
            var handler = sp.getRequiredService(handlerType);
            return (message, context) -> {
                @SuppressWarnings("unchecked")
                var result = handler.handle((M) message, context);
                return result;
            };
        };

        concurrentDictionarySafeRegister(channel, factory);
        publisher.submit(new MessageSubscribedEvent(channel, messageType, handlerType));
    }

    /**
     * Registers a handler described by a {@link HandlerRegistration}.
     * The registration contains the handler type, the method to invoke, and the channel name.
     *
     * @param registration the {@link HandlerRegistration} describing the handler to register
     */
    void register(HandlerRegistration registration) {
        MessageHandlerFactory factory = sp -> {
            var handler = sp.getServiceOrCreate(registration.handlerType());
            return (message, context) -> {
                var method = registration.method();
                var args = resolveArguments(method, message, context);
                try {
                    method.setAccessible(true);
                    return method.invoke(handler, args);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException("Failed to invoke handler method '" + method.getName() + "'", e);
                }
            };
        };

        concurrentDictionarySafeRegister(registration.channel(), factory);
        publisher.submit(new MessageSubscribedEvent(
            registration.channel(),
            registration.messageType(),
            registration.handlerType()));
    }

    // endregion

    // region Message handling

    @Override
    public CompletableFuture<Void> handleAsync(Object message, MessageContext context) {
        if (message == null) {
            return CompletableFuture.completedFuture(null);
        }

        var channel = MessageCache.getInstance().getOrAddChannel(message.getClass());
        return handleAsync(channel, message, context);
    }

    @Override
    public CompletableFuture<Void> handleAsync(String channel, Object message, MessageContext context) {
        if (message == null) {
            return CompletableFuture.completedFuture(null);
        }

        var factories = handlerContainer.get(channel);
        if (factories == null || factories.isEmpty()) {
            LOGGER.warning(() -> "No handler registered for message " + context.getMessageId()
                + " on channel " + channel);
            return CompletableFuture.failedFuture(new IllegalStateException(
                "No handler registered for message " + context.getMessageId() + " on channel " + channel));
        }

        LOGGER.info(() -> "Message " + context.getMessageId() + " is being handled");

        if (factories.size() == 1) {
            // Single handler — support request/response pattern
            var factory = factories.get(0);
            return executeHandler(factory, message, context)
                .<Void>handle((result, ex) -> {
                    if (ex != null) {
                        var cause = unwrapCause(ex);
                        if (cause instanceof InternalServerErrorException) {
                            context.failure(cause);
                        } else {
                            context.failure(new InternalServerErrorException(
                                "Error handling message " + context.getMessageId(), cause));
                        }
                    } else if (result != null) {
                        context.response(result);
                    }
                    return null;
                })
                .whenComplete((v, ex) -> LOGGER.info(() -> "Message " + context.getMessageId() + " was completed handled"));
        }

        // Multiple handlers — execute all in parallel (multicast scenario)
        var futures = factories.stream()
                               .map(factory -> executeHandler(factory, message, context)
                                   .thenAccept(result -> { /* multicast — results are discarded */ }))
                               .toArray(CompletableFuture[]::new);

        return CompletableFuture.allOf(futures)
                                .whenComplete((v, ex) ->
                                    LOGGER.info(() -> "Message " + context.getMessageId() + " was completed handled"));
    }

    /**
     * Executes a single handler factory within the given service scope.
     * For request/unicast message types, exceptions are rethrown.
     * For multicast message types, exceptions are swallowed and returned as the result.
     */
    private CompletableFuture<Object> executeHandler(MessageHandlerFactory factory,
                                                     Object message,
                                                     MessageContext context) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                var handler = factory.createHandler(provider);
                return handler.handle(message, context);
            } catch (Exception exception) {
                LOGGER.log(Level.SEVERE, exception,
                    () -> "Error occurred while handling message " + context.getMessageId());
                if (convention.isRequestType(message.getClass())
                    || convention.isUnicastType(message.getClass())) {
                    // Swallow the exception for request/response and unicast messages
                    throw new RuntimeException(exception);
                }
                return exception;
            }
        });
    }

    // endregion

    // region Internal helpers

    /**
     * Safely registers a value into a concurrent map whose values are lists.
     * This method uses a lock on the internal handler container to ensure that list
     * mutations (add/replace) are thread-safe for the combined key/value operations.
     *
     * @param key   the key to register the value under
     * @param value the value to add to the list for the given key
     */
    private void concurrentDictionarySafeRegister(String key, MessageHandlerFactory value) {
        synchronized (handlerContainer) {
            handlerContainer.compute(key, (k, list) -> {
                if (list == null) {
                    var newList = new ArrayList<MessageHandlerFactory>();
                    newList.add(value);
                    return newList;
                }
                if (!list.contains(value)) {
                    list.add(value);
                }
                return list;
            });
        }
    }

    /**
     * Resolves the arguments for invoking a handler method.
     * The method supports up to three parameters where parameter positions are resolved by type:
     * <ul>
     *   <li>A parameter matching {@link MessageContext} receives the provided {@code context} instance.</li>
     *   <li>Any other parameter receives the {@code message} instance.</li>
     * </ul>
     *
     * @param method  the {@link Method} representing the handler method to invoke
     * @param message the message object to be passed to the handler
     * @param context the {@link MessageContext} to be passed to the handler when requested
     * @return an array of arguments corresponding to the method parameters
     */
    private static Object[] resolveArguments(Method method, Object message, MessageContext context) {
        var parameterTypes = method.getParameterTypes();
        var arguments = new Object[parameterTypes.length];

        switch (parameterTypes.length) {
            case 0:
                break;
            case 1:
                if (parameterTypes[0] == MessageContext.class) {
                    arguments[0] = context;
                } else {
                    arguments[0] = message;
                }
                break;
            case 2:
            case 3:
                arguments[0] = message;
                for (var i = 1; i < parameterTypes.length; i++) {
                    if (parameterTypes[i] == MessageContext.class) {
                        arguments[i] = context;
                    }
                }
                break;
            default:
                return new Object[0];
        }

        return arguments;
    }

    /**
     * Unwraps the cause from a {@link java.util.concurrent.CompletionException} if present.
     */
    private static Throwable unwrapCause(Throwable ex) {
        var cause = ex;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }

    // endregion
}
