package com.euonia.http;

/**
 * Defines the interface of a request context accessor.
 */
public interface RequestContextAccessor {
    /**
     * Get the current request context.
     *
     * @return the current request context
     */
    RequestContext getContext();
}
