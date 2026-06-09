package com.euonia.osba;

import com.euonia.factory.BusinessObjectFactory;
import com.euonia.factory.ObjectFactory;
import com.euonia.reflection.ServiceResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OsbaConfiguration {
    @Bean
    public ObjectFactory objectFactory(ServiceResolver resolver) {
        var factory = new BusinessObjectFactory();
        factory.use(resolver);
        return factory;
    }
}
