package com.euonia.http;

import java.util.Map;

import com.euonia.security.UserPrincipal;

/**
 * Contains information about the current request.
 */
public final class RequestContext {

    private String connectionId;
    private String requestUri;
    private String requestMethod;
    private String remoteIpAddress;
    private int remotePort;
    private boolean webSocketRequest;
    private UserPrincipal user;
    private Map<String, String> requestHeaders;
    private String traceIdentifier;

    /**
     * Gets or sets a unique identifier to represent the connection.
     */
    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    /**
     * Gets or sets the IP address of the remote target. Can be null.
     */
    public String getRemoteIpAddress() {
        return remoteIpAddress;
    }

    public void setRemoteIpAddress(String remoteIpAddress) {
        this.remoteIpAddress = remoteIpAddress;
    }

    /**
     * Gets or sets the port of the remote target.
     */
    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    /**
     * Gets a value indicating whether the request is a WebSocket establishment request.
     */
    public boolean isWebSocketRequest() {
        return webSocketRequest;
    }

    public void setWebSocketRequest(boolean webSocketRequest) {
        this.webSocketRequest = webSocketRequest;
    }

    /**
     * Gets or sets the user for this request.
     */
    public UserPrincipal getUser() {
        return user;
    }

    public void setUser(UserPrincipal user) {
        this.user = user;
    }

    /**
     * Gets the request headers.
     */
    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    /**
     * Gets the Authorization HTTP header.
     */
    public String getAuthorization() {
        return requestHeaders != null ? requestHeaders.get("Authorization") : null;
    }

    /**
     * Gets the Request-Id HTTP header.
     */
    public String getRequestId() {
        return requestHeaders != null ? requestHeaders.get("Request-Id") : null;
    }

    /**
     * Gets or sets a unique identifier to represent this request in trace logs.
     */
    public String getTraceIdentifier() {
        return traceIdentifier;
    }

    public void setTraceIdentifier(String traceIdentifier) {
        this.traceIdentifier = traceIdentifier;
    }
}
