package com.euonia.bus;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Consumer;

import com.euonia.bus.event.MessageHandledEvent;
import com.euonia.bus.event.MessageRepliedEvent;
import com.euonia.security.UserPrincipal;

/**
 * The built-in implementation of {@link MessageContext}.
 * <p>
 * Provides a thread-safe message handling context that supports event-based
 * notification for replied, completed, and failed states. On {@link #close()},
 * the context automatically invokes {@link #complete(Object)} with the original
 * message to signal the end of message processing.
 *
 * @see MessageContext
 */
public final class MessageContextBase implements MessageContext {

    private final Object message;
    private final Map<String, String> headers = new java.util.HashMap<>();
    private final UserPrincipal user;
    private MessageMetadata metadata;
    private boolean disposed;

    private final ConcurrentHashMap<String, SubmissionPublisher<?>> publishers = new ConcurrentHashMap<>();

    /**
     * Initializes a new instance with the specified message.
     *
     * @param message the message payload
     */
    public MessageContextBase(Object message) {
        this.message = message;
        this.user = null;
    }

    /**
     * Initializes a new instance from a routed message envelope, copying all
     * envelope metadata (IDs, authorization) into the context.
     *
     * @param pack the routed message envelope
     */
    public MessageContextBase(RoutedMessage<?> pack) {
        this.message = pack.getPayload();
        this.user = null; // RoutedMessage does not yet carry user information
        headers.put(MessageHeaders.MESSAGE_ID, pack.getMessageId());
        headers.put(MessageHeaders.CORRELATION_ID, pack.getCorrelationId());
        headers.put(MessageHeaders.CONVERSATION_ID, pack.getConversationId());
        headers.put(MessageHeaders.REQUEST_TRACE_ID, pack.getRequestTrackId());
        headers.put(MessageHeaders.AUTHORIZATION, pack.getAuthorization());
        this.metadata = pack.getMetadata();
    }

    // ---- Listener registration (used by bus infrastructure) ----

    @SuppressWarnings({"unchecked", "resource"})
    public <E> void addListener(String event, Consumer<E> listener) {
        SubmissionPublisher<E> publisher = (SubmissionPublisher<E>) publishers.computeIfAbsent(event, e -> new SubmissionPublisher<>());
        publisher.consume(listener);
    }

    // ---- MessageContext implementation ----

    @Override
    public Object getMessage() {
        return message;
    }

    @Override
    public String getMessageId() {
        return headers.getOrDefault(MessageHeaders.MESSAGE_ID, null);
    }

    @Override
    public void setMessageId(String messageId) {
        headers.put(MessageHeaders.MESSAGE_ID, messageId);
    }

    @Override
    public String getCorrelationId() {
        return headers.getOrDefault(MessageHeaders.CORRELATION_ID, null);
    }

    @Override
    public void setCorrelationId(String correlationId) {
        headers.put(MessageHeaders.CORRELATION_ID, correlationId);
    }

    @Override
    public String getConversationId() {
        return headers.getOrDefault(MessageHeaders.CONVERSATION_ID, null);
    }

    @Override
    public void setConversationId(String conversationId) {
        headers.put(MessageHeaders.CONVERSATION_ID, conversationId);
    }

    @Override
    public String getRequestTraceId() {
        return headers.getOrDefault(MessageHeaders.REQUEST_TRACE_ID, null);
    }

    @Override
    public void setRequestTraceId(String requestTraceId) {
        headers.put(MessageHeaders.REQUEST_TRACE_ID, requestTraceId);
    }

    @Override
    public String getAuthorization() {
        return headers.getOrDefault(MessageHeaders.AUTHORIZATION, null);
    }

    @Override
    public void setAuthorization(String authorization) {
        headers.put(MessageHeaders.AUTHORIZATION, authorization);
    }

    @Override
    public UserPrincipal getUser() {
        return user;
    }

    @Override
    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    @Override
    public MessageMetadata getMetadata() {
        return metadata;
    }

    @Override
    public void setMetadata(MessageMetadata metadata) {
        this.metadata = metadata;
    }

    // ---- Lifecycle methods ----

    /**
     * Fires the replied event, notifying listeners that the message
     * has been replied to with the given response.
     *
     * @param message the response message
     */
    @SuppressWarnings("unchecked")
    @Override
    public <R> void response(R message) {
        MessageRepliedEvent args = new MessageRepliedEvent(message);
        var publisher = (SubmissionPublisher<MessageRepliedEvent>) publishers.getOrDefault("Responded", null);
        if (publisher != null) {
            publisher.submit(args);
        }
    }

    /**
     * Fires the failed event, notifying listeners that message handling
     * encountered an error.
     *
     * @param throwable the exception that caused the failure
     */
    @SuppressWarnings("unchecked")
    @Override
    public void failure(Throwable throwable) {
        var publisher = (SubmissionPublisher<Throwable>) publishers.getOrDefault("Failed", null);
        if (publisher != null) {
            publisher.submit(throwable);
        }
    }

    /**
     * Fires the completed event, notifying listeners that the message
     * has been fully processed.
     *
     * @param message the processed message
     */
    @SuppressWarnings("unchecked")
    @Override
    public void complete(Object message) {
        MessageHandledEvent args = new MessageHandledEvent(message);
        var publisher = (SubmissionPublisher<MessageHandledEvent>) publishers.getOrDefault("Completed", null);
        if (publisher != null) {
            publisher.submit(args);
        }
    }

    /**
     * Fires the completed event with the handler type information.
     *
     * @param message     the processed message
     * @param handlerType the type of the handler that processed the message
     */
    @SuppressWarnings("unchecked")
    @Override
    public void complete(Object message, Class<?> handlerType) {
        MessageHandledEvent args = new MessageHandledEvent(message);
        args.setHandlerType(handlerType);
        var publisher = (SubmissionPublisher<MessageHandledEvent>) publishers.getOrDefault("Completed", null);
        if (publisher != null) {
            publisher.submit(args);
        }
    }

    /**
     * Disposes the context, firing the completed event with the original
     * message and cleaning up all registered listeners.
     * <p>
     * This mirrors the .NET dispose pattern: on close, the context signals
     * completion of message processing and releases all event handlers.
     */
    @Override
    public void close() {
        if (disposed) {
            return;
        }
        disposed = true;

        // Signal completion with the original message, matching .NET Dispose pattern
        complete(message);

        // Release all listeners to prevent memory leaks
        publishers.values().forEach(SubmissionPublisher::close);
        publishers.clear();
    }
}
