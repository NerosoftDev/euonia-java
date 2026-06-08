package com.euonia.pipeline;

import com.euonia.reflection.ServiceResolver;

public class DefaultPipelineFactory implements PipelineFactory {
    private final ServiceResolver resolver;

    public DefaultPipelineFactory(ServiceResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public Pipeline create() {
        return new DefaultPipelineProvider(resolver);
    }

    @Override
    public <TRequest, TResponse> RequestResponsePipeline<TRequest, TResponse> createRequestResponse() {
        return new DefaultRequestResponsePipelineProvider<>(resolver);
    }
}
