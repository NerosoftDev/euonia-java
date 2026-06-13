package com.euonia.pipeline;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.euonia.reflection.SimpleServiceProvider;

class DefaultPipelineProviderTypedTests {

    @Test
    void should_wrap_accumulate_with_behavior() {
        DefaultPipelineProvider<Integer, Integer> pipeline =
                new DefaultPipelineProvider<>(new SimpleServiceProvider());

        pipeline.use(PlusOneBehavior.class);

        int result = pipeline.runAsync(2, request -> CompletableFuture.completedFuture(request * 2))
                .toCompletableFuture()
                .join();

        assertEquals(5, result);
    }

    @Test
    void should_resolve_dependency_parameter_for_reflection_handler() {
        SimpleServiceProvider resolver = new SimpleServiceProvider();
        SuffixService suffixService = new SuffixService("-ok");
        resolver.register(SuffixService.class, suffixService);

        DefaultPipelineProvider<String, String> pipeline =
                new DefaultPipelineProvider<>(resolver);
        pipeline.use(ReflectionPipelineBehavior.class);

        String value = pipeline.runAsync("input", CompletableFuture::completedFuture)
                .toCompletableFuture()
                .join();

        assertEquals("input-ok", value);
    }

    static class PlusOneBehavior implements PipelineBehavior<Integer, Integer> {
        @Override
        public CompletionStage<Integer> handleAsync(Integer context, PipelineDelegate<Integer, Integer> next) {
            return next.invoke(context).thenApply(value -> value + 1);
        }
    }

    static class SuffixService {
        private final String suffix;

        SuffixService(String suffix) {
            this.suffix = suffix;
        }
    }

    static class ReflectionPipelineBehavior {
        private final PipelineDelegate<String, String> next;

        ReflectionPipelineBehavior(PipelineDelegate<String, String> next) {
            this.next = next;
        }

        public CompletionStage<String> handleAsync(String context, SuffixService suffixService) {
            return next.invoke(context).thenApply(value -> value + suffixService.suffix);
        }
    }
}
