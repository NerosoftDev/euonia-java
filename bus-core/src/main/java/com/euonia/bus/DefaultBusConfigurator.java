package com.euonia.bus;

import com.euonia.bus.convention.MessageConventionBuilder;
import com.euonia.bus.strategy.TransportStrategyBuilder;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * DefaultBusConfigurator is the default implementation of the BusConfigurator interface.
 * It provides a fluent API for configuring message conventions, transport strategies, and handler registrations.
 */
public class DefaultBusConfigurator implements BusConfigurator {
    private final MessageConventionBuilder conventionBuilder = new MessageConventionBuilder();
    private final ConcurrentMap<String, TransportStrategyBuilder> strategyBuilders = new ConcurrentHashMap<>();
    private final List<HandlerRegistration> registrations = new java.util.concurrent.CopyOnWriteArrayList<>();

    @Override
    public MessageConventionBuilder getConventionBuilder() {
        return conventionBuilder;
    }

    @Override
    public ConcurrentMap<String, TransportStrategyBuilder> getStrategyBuilders() {
        return strategyBuilders;
    }

    @Override
    public List<HandlerRegistration> getRegistrations() {
        return registrations;
    }

    public DefaultBusConfigurator setConvention(Consumer<MessageConventionBuilder> conventionConfig) {
        assert conventionConfig != null : "Convention configuration cannot be null";
        conventionConfig.accept(conventionBuilder);
        return this;
    }

    public DefaultBusConfigurator setStrategy(String name, Consumer<TransportStrategyBuilder> strategyConfig) {
        assert name != null && !name.trim().isEmpty() : "Strategy name cannot be null or empty";
        assert strategyConfig != null : "Strategy configuration cannot be null";
        var builder = strategyBuilders.computeIfAbsent(name, k -> new TransportStrategyBuilder());
        strategyConfig.accept(builder);
        return this;
    }

    public DefaultBusConfigurator registerHandlers(HandlerRegistration registration) {
        assert registration != null : "Message registration cannot be null";
        registrations.add(registration);
        return this;
    }

    public DefaultBusConfigurator registerHandlers(List<Class<?>> types) {
        assert types != null && !types.isEmpty() : "Types cannot be null or empty";
        var registrations = MessageHandlerFinder.find(types.toArray(new Class<?>[0]));
        this.registrations.addAll(registrations);
        return this;
    }

    public DefaultBusConfigurator registerHandlers(Class<?>... types) {
        assert types != null && types.length > 0 : "Types cannot be null or empty";
        var registrations = MessageHandlerFinder.find(types);
        this.registrations.addAll(registrations);
        return this;
    }

    public DefaultBusConfigurator registerHandlers(String... packageNames) {
        assert packageNames != null && packageNames.length > 0 : "Package names cannot be null or empty";

        for (String packageName : packageNames) {
            assert packageName != null && !packageName.trim().isEmpty() : "Package name cannot be null or empty";
            var registrations = MessageHandlerFinder.find(packageName);
            this.registrations.addAll(registrations);
        }
        return this;
    }
}
