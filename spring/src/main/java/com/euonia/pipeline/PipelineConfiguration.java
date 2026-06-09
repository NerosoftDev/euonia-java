package com.euonia.pipeline;

import com.euonia.spring.BeanScope;
import com.euonia.reflection.ServiceResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class PipelineConfiguration {
    @Bean
    @Scope(BeanScope.PROTOTYPE)
    public PipelineFactory pipelineFactory(ServiceResolver resolver) {
        return new DefaultPipelineFactory(resolver);
    }

    @Bean
    @Scope(BeanScope.PROTOTYPE)
    public Pipeline pipeline(ServiceResolver resolver) {
        return new DefaultPipelineProvider(resolver);
    }

    @Bean
    @Scope(BeanScope.PROTOTYPE)
    public PipelineDelegate pipelineDelegate(Pipeline pipeline) {
        return pipeline.build();
    }
}
