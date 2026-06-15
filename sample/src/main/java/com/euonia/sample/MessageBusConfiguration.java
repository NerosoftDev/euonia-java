package com.euonia.sample;

import com.euonia.bus.BusConfigurator;
import com.euonia.bus.DefaultBusConfigurator;
import com.euonia.bus.convention.AnnotationMessageConvention;
import com.euonia.bus.convention.DefaultMessageConvention;
import com.euonia.bus.strategy.AnnotationTransportStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageBusConfiguration {
    @Bean
    public BusConfigurator busConfigurator() {

        var packageName = this.getClass().getPackageName();

        return new DefaultBusConfigurator()
            .setConvention(c -> {
                c.add(DefaultMessageConvention.class);
                c.add(AnnotationMessageConvention.class);
                c.evaluateUnicast(t -> t.getPackageName().startsWith(packageName) && t.getSimpleName().endsWith("Command"));
                c.evaluateMulticast(t -> t.getPackageName().startsWith(packageName) && t.getSimpleName().endsWith("Event"));
                c.evaluateRequest(t -> t.getPackageName().startsWith(packageName) && t.getSimpleName().endsWith("Query"));
            })
            .setStrategy("InMemory", s -> {
                s.add(new AnnotationTransportStrategy("InMemory"));
                s.evaluateOutgoing(t -> t.getPackageName().startsWith(packageName) && t.getSimpleName().endsWith("Command"));
                s.evaluateIncoming(t -> t.getPackageName().startsWith(packageName) && t.getSimpleName().endsWith("Command"));
            })
            .registerHandlers(packageName + ".application.handler");
    }
}
