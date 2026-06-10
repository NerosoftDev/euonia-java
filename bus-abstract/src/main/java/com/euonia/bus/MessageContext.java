package com.euonia.bus;

import com.euonia.security.UserPrincipal;

import java.util.Map;

public interface MessageContext extends AutoCloseable {
    Object getMessage();

    String getMessageId();

    void setMessageId(String messageId);

    String getCorrelationId();

    void setCorrelationId(String correlationId);

    String getConversationId();

    void setConversationId(String conversationId);

    String getRequestTraceId();

    void setRequestTraceId(String requestTraceId);

    String getAuthorization();

    void setAuthorization(String authorization);

    UserPrincipal getUser();

    Map<String, String> getHeaders();

    MessageMetadata getMetadata();

    void setMetadata(MessageMetadata metadata);

    <R> void response(R message);

    void failure(Throwable throwable);

    void complete(Object message);

    void complete(Object message, Class<?> handlerType);
}
