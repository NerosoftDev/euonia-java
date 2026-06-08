package com.euonia.pipeline;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.euonia.reflection.SimpleServiceResolver;

class DefaultRequestResponsePipelineProviderTests {

    @Test
    void should_wrap_accumulate_with_behavior() {
        DefaultRequestResponsePipelineProvider<Integer, Integer> pipeline =
                new DefaultRequestResponsePipelineProvider<>(new SimpleServiceResolver());

        pipeline.use(PlusOneBehavior.class);

        int result = pipeline.runAsync(2, request -> CompletableFuture.completedFuture(request * 2))
                .toCompletableFuture()
                .join();

        assertEquals(5, result);
    }

    @Test
    void should_resolve_dependency_parameter_for_reflection_handler() {
        SimpleServiceResolver resolver = new SimpleServiceResolver();
        SuffixService suffixService = new SuffixService("-ok");
        resolver.register(SuffixService.class, suffixService);

        DefaultRequestResponsePipelineProvider<String, String> pipeline =
                new DefaultRequestResponsePipelineProvider<>(resolver);
        pipeline.use(ReflectionRequestResponseBehavior.class);

        String value = pipeline.runAsync("input", CompletableFuture::completedFuture)
                .toCompletableFuture()
                .join();

        assertEquals("input-ok", value);
    }

    static class PlusOneBehavior implements RequestResponsePipelineBehavior<Integer, Integer> {
        @Override
        public CompletionStage<Integer> handleAsync(Integer context, RequestResponsePipelineDelegate<Integer, Integer> next) {
            return next.invoke(context).thenApply(value -> value + 1);
        }
    }

    static class SuffixService {
        private final String suffix;

        SuffixService(String suffix) {
            this.suffix = suffix;
        }
    }

    static class ReflectionRequestResponseBehavior {
        private final RequestResponsePipelineDelegate<String, String> next;

        ReflectionRequestResponseBehavior(RequestResponsePipelineDelegate<String, String> next) {
            this.next = next;
        }

        public CompletionStage<String> handleAsync(String context, SuffixService suffixService) {
            return next.invoke(context).thenApply(value -> value + suffixService.suffix);
        }
    }
}
