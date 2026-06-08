package com.euonia.pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.euonia.reflection.SimpleServiceResolver;

class DefaultPipelineProviderTests {

    @Test
    void should_run_attribute_behavior_before_accumulate() {
        DefaultPipelineProvider pipeline = new DefaultPipelineProvider(new SimpleServiceResolver());
        TraceContext context = new TraceContext();

        pipeline.runAsync(context, ignored -> {
            context.trace.add("accumulate");
            return CompletableFuture.completedFuture(null);
        }).toCompletableFuture().join();

        assertEquals(List.of("behavior", "accumulate"), context.trace);
    }

    @Test
    void should_resolve_method_parameters_from_service_resolver() {
        SimpleServiceResolver resolver = new SimpleServiceResolver();
        MarkerService markerService = new MarkerService();
        resolver.register(MarkerService.class, markerService);

        DefaultPipelineProvider pipeline = new DefaultPipelineProvider(resolver);
        pipeline.use(ReflectionPipelineBehavior.class);

        pipeline.runAsync("ctx").toCompletableFuture().join();

        assertEquals("ctx", markerService.value);
    }

    @PipelineBehaviors(AnnotatedPipelineBehavior.class)
    static class TraceContext {
        final List<String> trace = new ArrayList<>();
    }

    static class AnnotatedPipelineBehavior implements PipelineBehavior {
        @Override
        public CompletionStage<Void> handleAsync(Object context, PipelineDelegate next) {
            TraceContext traceContext = (TraceContext) context;
            traceContext.trace.add("behavior");
            return next.invoke(context);
        }
    }

    static class MarkerService {
        private String value;
    }

    static class ReflectionPipelineBehavior {
        private final PipelineDelegate next;

        ReflectionPipelineBehavior(PipelineDelegate next) {
            this.next = next;
        }

        public CompletionStage<Void> handleAsync(Object context, MarkerService markerService) {
            markerService.value = String.valueOf(context);
            return next.invoke(context);
        }
    }
}
