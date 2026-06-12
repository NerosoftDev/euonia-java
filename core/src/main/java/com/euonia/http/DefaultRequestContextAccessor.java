package com.euonia.http;

public class DefaultRequestContextAccessor {
    private final ThreadLocal<RequestContext> requestContext = new ThreadLocal<>();

    public void setContext(RequestContext context) {
        requestContext.set(context);
    }

    public RequestContext getContext() {
        return requestContext.get();
    }
}
