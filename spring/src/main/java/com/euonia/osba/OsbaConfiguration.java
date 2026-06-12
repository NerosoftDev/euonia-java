package com.euonia.osba;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.euonia.factory.BusinessObjectFactory;
import com.euonia.factory.ObjectFactory;
import com.euonia.reflection.ServiceProvider;

@Configuration
public class OsbaConfiguration {
    @Bean
    public ObjectFactory objectFactory(ServiceProvider resolver) {
        var factory = new BusinessObjectFactory();
        factory.use(resolver);
        return factory;
    }
}
