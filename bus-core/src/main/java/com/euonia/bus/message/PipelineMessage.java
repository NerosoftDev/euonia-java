package com.euonia.bus.message;

import com.euonia.pipeline.Pipeline;

import java.util.concurrent.CompletableFuture;

public class PipelineMessage<M, R> {
    private final M message;
    private Pipeline<M, R> pipeline;

    public PipelineMessage(M message) {
        this.message = message;
    }

    public PipelineMessage(M message, Pipeline<M, R> pipeline) {
        this(message);
        this.pipeline = pipeline;
    }

    public M getMessage() {
        return message;
    }

    public Pipeline<M, R> getPipeline() {
        return pipeline;
    }

    public PipelineMessage<M, R> use(Class<?> type, Object... args) {
        pipeline = pipeline.use(type, args);
        return this;
    }

    public CompletableFuture<R> executeAsync() {
        return pipeline.runAsync(message)
                       .toCompletableFuture();
    }

}
