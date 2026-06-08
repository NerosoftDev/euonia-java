package com.euonia.reflection;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceResolverConfiguration {
    @Bean
    public ServiceResolver serviceResolver(ApplicationContext applicationContext) {
        return new ApplicationContextServiceResolver(applicationContext);
    }
}
