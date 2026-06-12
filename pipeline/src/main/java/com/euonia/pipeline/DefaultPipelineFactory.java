package com.euonia.pipeline;

import com.euonia.reflection.ServiceProvider;

public class DefaultPipelineFactory implements PipelineFactory {
    private final ServiceProvider provider;

    public DefaultPipelineFactory(ServiceProvider provider) {
        this.provider = provider;
    }

    @Override
    public Pipeline create() {
        return new DefaultPipelineProvider(provider);
    }

    @Override
    public <TRequest, TResponse> RequestResponsePipeline<TRequest, TResponse> createRequestResponse() {
        return new DefaultRequestResponsePipelineProvider<>(provider);
    }
}
