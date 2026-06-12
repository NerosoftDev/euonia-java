package com.euonia.reflection;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceProviderConfiguration {
    @Bean
    public ServiceProvider serviceProvider(ApplicationContext applicationContext) {
        return new ApplicationContextServiceProvider(applicationContext);
    }
}
