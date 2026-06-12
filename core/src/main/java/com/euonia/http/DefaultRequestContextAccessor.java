package com.euonia.http;

public class DefaultRequestContextAccessor implements RequestContextAccessor {
    private final ThreadLocal<RequestContext> requestContext = new ThreadLocal<>();

    public void setContext(RequestContext context) {
        requestContext.set(context);
    }

    @Override
    public RequestContext getContext() {
        return requestContext.get();
    }

    public void setRequestContext(RequestContext requestContext) {
        this.requestContext.set(requestContext);
    }
}
