package com.euonia.pipeline;

import java.util.concurrent.CompletionStage;

@FunctionalInterface
public interface RequestPipelineDelegate<TRequest> {
    CompletionStage<Void> invoke(TRequest request);
}
