package com.euonia.pipeline;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.euonia.reflection.ServiceProvider;
import com.euonia.spring.BeanScope;

@Configuration
public class PipelineConfiguration {
    @Bean
    @Scope(BeanScope.PROTOTYPE)
    public PipelineFactory pipelineFactory(ServiceProvider resolver) {
        return new DefaultPipelineFactory(resolver);
    }

    @Bean
    @Scope(BeanScope.PROTOTYPE)
    @SuppressWarnings("rawtypes")
    public Pipeline pipeline(ServiceProvider resolver) {
        return new DefaultPipelineProvider<>(resolver);
    }

    @Bean
    @Scope(BeanScope.PROTOTYPE)
    @SuppressWarnings({"rawtypes", "unchecked"})
    public PipelineDelegate pipelineDelegate(Pipeline pipeline) {
        return pipeline.build();
    }
}
