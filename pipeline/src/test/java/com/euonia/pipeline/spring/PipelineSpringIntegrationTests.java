/*
package com.euonia.pipeline.spring;

import java.util.concurrent.CompletionStage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.euonia.pipeline.Pipeline;
import com.euonia.pipeline.PipelineDelegate;
import com.euonia.pipeline.PipelineFactory;
import com.euonia.reflection.ServiceProvider;

class PipelineSpringIntegrationTests {

    @Test
    void should_create_pipeline_through_spring_configuration() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfiguration.class)) {
            PipelineFactory factory = context.getBean(PipelineFactory.class);
            ServiceProvider provider = context.getBean(ServiceProvider.class);

            assertNotNull(factory);
            assertNotNull(provider);

            PrefixService prefixService = context.getBean(PrefixService.class);
            Pipeline pipeline = factory.create();
            pipeline.use(SpringReflectionBehavior.class);
            pipeline.runAsync("value").toCompletableFuture().join();

            assertEquals("prefix-value", prefixService.recorded);
        }
    }

    @Configuration
    @Import(PipelineConfiguration.class)
    static class TestConfiguration {
        @Bean
        PrefixService prefixService() {
            return new PrefixService();
        }
    }

    static class PrefixService {
        private String recorded;
    }

    static class SpringReflectionBehavior {
        private final PipelineDelegate next;

        SpringReflectionBehavior(PipelineDelegate next) {
            this.next = next;
        }

        public CompletionStage<Void> handleAsync(Object context, PrefixService prefixService) {
            prefixService.recorded = "prefix-" + context;
            return next.invoke(context);
        }
    }
}
*/
