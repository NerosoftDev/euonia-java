package com.euonia.pipeline;

import com.euonia.reflection.ServiceProvider;

public class DefaultPipelineFactory implements PipelineFactory {
    private final ServiceProvider provider;

    public DefaultPipelineFactory(ServiceProvider provider) {
        this.provider = provider;
    }

    @Override
    public <C, R> Pipeline<C, R> create() {
        return new DefaultPipelineProvider<>(provider);
    }
}
