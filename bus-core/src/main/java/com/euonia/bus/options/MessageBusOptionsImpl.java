package com.euonia.bus.options;

import com.euonia.bus.BusConfigurator;
import com.euonia.bus.convention.MessageConvention;
import com.euonia.bus.strategy.TransportStrategy;

import java.util.List;

public final class MessageBusOptionsImpl implements MessageBusOptions {
    private final BusConfigurator configurator;
    private String defaultTransport;

    private boolean isEnablePipelineBehaviors = true;

    public MessageBusOptionsImpl(BusConfigurator configurator) {
        this.configurator = configurator;
    }

    @Override
    public String getDefaultTransport() {
        return defaultTransport;
    }

    public void setDefaultTransport(String defaultTransport) {
        this.defaultTransport = defaultTransport;
    }

    @Override
    public boolean isEnablePipelineBehaviors() {
        return isEnablePipelineBehaviors;
    }

    @Override
    public MessageConvention getConvention() {
        return configurator.getConventionBuilder().getConvention();
    }

    @Override
    public List<String> getStrategyAssignedTypes() {
        return configurator.getStrategyBuilders().keySet().stream().toList();
    }

    @Override
    public TransportStrategy getStrategy(String transport) {
        var builder = configurator.getStrategyBuilders().get(transport);
        return builder == null ? null : builder.getStrategy();
    }

    public void setEnablePipelineBehaviors(boolean enablePipelineBehaviors) {
        isEnablePipelineBehaviors = enablePipelineBehaviors;
    }
}
