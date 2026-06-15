package com.euonia.http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Configuration for request context related beans.
 */
@Configuration
public class RequestContextConfiguration {

    @Bean
    @RequestScope
    public RequestContextAccessor requestContextAccessor(RequestContext context) {
        var accessor = new DefaultRequestContextAccessor();
        accessor.setRequestContext(context);
        return accessor;
    }
}
