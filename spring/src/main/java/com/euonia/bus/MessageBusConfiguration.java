package com.euonia.bus;

import com.euonia.bus.options.MessageBusOptions;
import com.euonia.bus.options.MessageBusOptionsImpl;
import com.euonia.reflection.ServiceProvider;
import com.euonia.spring.BeanScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.lang.reflect.InvocationTargetException;

@Configuration
public class MessageBusConfiguration {
    @Value("${euonia.bus.default-transport:}")
    private String defaultTransport;

    @Value("${euonia.bus.pipeline-behaviors:true}")
    private boolean isEnablePipelineBehaviors;

    @Bean
    @Scope(BeanScope.PROTOTYPE)
    public Bus bus(ServiceProvider provider) {
        return new MessageBus(provider, dispatcher(), messageBusOptions());
    }

    @Bean
    public Dispatcher dispatcher() {
        return new StrategicDispatcher(messageBusOptions());
    }

    @Bean
    public MessageBusOptions messageBusOptions() {
        var options = new MessageBusOptionsImpl();
        options.setDefaultTransport(defaultTransport);
        options.setEnablePipelineBehaviors(isEnablePipelineBehaviors);
        return options;
    }

    @Bean
    public HandlerContext handlerContext(ServiceProvider provider) {

        var context = new DefaultHandlerContext(provider);

        var configurator = provider.getService(BusConfigurator.class)
                                   .orElse(null);

        try {
            var genericRegisterMethod = DefaultHandlerContext.class.getDeclaredMethod("register", Class.class, Class.class);
            if (configurator != null) {
                for (var registration : configurator.getRegistrations()) {
                    if (registration.method().getAnnotation(Override.class) != null) {
                        genericRegisterMethod.invoke(context, registration.messageType(), registration.handlerType());
                    } else {
                        context.register(registration);
                    }
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        return context;
    }
}
