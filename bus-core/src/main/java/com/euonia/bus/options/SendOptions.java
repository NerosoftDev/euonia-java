package com.euonia.bus.options;

public class SendOptions extends ExtendableOptions {
    private String correlationId;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}
