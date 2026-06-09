package com.euonia.sample.controller;

import com.euonia.pipeline.PipelineFactory;
import com.euonia.pipeline.RequestResponsePipelineBehavior;
import com.euonia.pipeline.RequestResponsePipelineDelegate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.euonia.pipeline.PipelineBehaviors;

@RestController
@RequestMapping("/api/pipeline")
public class PipelineDemoController {
    private final PipelineFactory pipelineFactory;

    public PipelineDemoController(PipelineFactory pipelineFactory) {
        this.pipelineFactory = pipelineFactory;
    }

    @GetMapping("/echo")
    public ResponseEntity<Map<String, Object>> echo(@RequestParam(defaultValue = "hello") String value) {
        EchoRequest request = new EchoRequest(value);

        var pipeline = pipelineFactory.<EchoRequest, String>createRequestResponse();

        pipeline.use(EchoSuffixBehavior.class);

        String result = pipeline.runAsync(request, r -> CompletableFuture.completedFuture(r.value()))
                .toCompletableFuture()
                .join();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("input", value);
        response.put("output", result);
        return ResponseEntity.ok(response);
    }

    @PipelineBehaviors({EchoUpperCaseBehavior.class})
    private record EchoRequest(String value) {
    }

    private static class EchoUpperCaseBehavior implements RequestResponsePipelineBehavior<EchoRequest, String> {
        @Override
        public CompletionStage<String> handleAsync(EchoRequest context, RequestResponsePipelineDelegate<EchoRequest, String> next) {
            return next.invoke(new EchoRequest(context.value().toUpperCase()));
        }
    }

    private static class EchoSuffixBehavior {
        private final RequestResponsePipelineDelegate<EchoRequest, String> next;

        private EchoSuffixBehavior(RequestResponsePipelineDelegate<EchoRequest, String> next) {
            this.next = next;
        }

        public CompletionStage<String> handleAsync(EchoRequest context) {
            return next.invoke(context).thenApply(value -> value + "-pipeline");
        }
    }
}
