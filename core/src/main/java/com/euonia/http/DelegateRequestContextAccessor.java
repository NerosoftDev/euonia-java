package com.euonia.http;

@FunctionalInterface
public interface DelegateRequestContextAccessor {
    RequestContext getContext();
}
