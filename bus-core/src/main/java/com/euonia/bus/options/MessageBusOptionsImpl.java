package com.euonia.bus.options;

public final class MessageBusOptionsImpl implements MessageBusOptions {
    private String defaultTransport;

    private boolean isEnablePipelineBehaviors = true;

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

    public void setEnablePipelineBehaviors(boolean enablePipelineBehaviors) {
        isEnablePipelineBehaviors = enablePipelineBehaviors;
    }
}
