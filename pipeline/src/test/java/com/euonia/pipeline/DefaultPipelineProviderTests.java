package com.euonia.pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.euonia.reflection.SimpleServiceProvider;

class DefaultPipelineProviderTests {

    @Test
    void should_run_attribute_behavior_before_accumulate() {
        DefaultPipelineProvider<TraceContext, Void> pipeline = new DefaultPipelineProvider<>(new SimpleServiceProvider());
        TraceContext context = new TraceContext();

        pipeline.runAsync(context, ignored -> {
            context.trace.add("accumulate");
            return CompletableFuture.completedFuture(null);
        }).toCompletableFuture().join();

        assertEquals(List.of("behavior", "accumulate"), context.trace);
    }

    @Test
    void should_resolve_method_parameters_from_service_resolver() {
        SimpleServiceProvider resolver = new SimpleServiceProvider();
        MarkerService markerService = new MarkerService();
        resolver.register(MarkerService.class, markerService);

        DefaultPipelineProvider<String, Void> pipeline = new DefaultPipelineProvider<>(resolver);
        pipeline.use(ReflectionPipelineBehavior.class);

        pipeline.runAsync("ctx").toCompletableFuture().join();

        assertEquals("ctx", markerService.value);
    }

    @PipelineBehaviors(AnnotatedPipelineBehavior.class)
    static class TraceContext {
        final List<String> trace = new ArrayList<>();
    }

    static class AnnotatedPipelineBehavior implements PipelineBehavior<TraceContext, Void> {
        @Override
        public CompletionStage<Void> handleAsync(TraceContext context, PipelineDelegate<TraceContext, Void> next) {
            context.trace.add("behavior");
            return next.invoke(context);
        }
    }

    static class MarkerService {
        private String value;
    }

    static class ReflectionPipelineBehavior {
        private final PipelineDelegate<String, Void> next;

        ReflectionPipelineBehavior(PipelineDelegate<String, Void> next) {
            this.next = next;
        }

        public CompletionStage<Void> handleAsync(String context, MarkerService markerService) {
            markerService.value = context;
            return next.invoke(context);
        }
    }
}
