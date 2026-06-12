package com.euonia.bus.options;

public abstract class ExtendableOptions {
    private String messageId;
    private String channel;
    private String queue;
    private int priority;
    private String requestTraceId;
    private Boolean enablePipelineBehaviors;
    private boolean attachDefaultPipelineBehaviors = true;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getRequestTraceId() {
        return requestTraceId;
    }

    public void setRequestTraceId(String requestTraceId) {
        this.requestTraceId = requestTraceId;
    }

    public Boolean getEnablePipelineBehaviors() {
        return enablePipelineBehaviors;
    }

    public void setEnablePipelineBehaviors(Boolean enablePipelineBehaviors) {
        this.enablePipelineBehaviors = enablePipelineBehaviors;
    }

    public boolean isAttachDefaultPipelineBehaviors() {
        return attachDefaultPipelineBehaviors;
    }

    public void setAttachDefaultPipelineBehaviors(boolean attachDefaultPipelineBehaviors) {
        this.attachDefaultPipelineBehaviors = attachDefaultPipelineBehaviors;
    }
}
