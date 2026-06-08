package com.euonia.pipeline;

import java.util.concurrent.CompletionStage;

@FunctionalInterface
public interface RequestResponsePipelineDelegate<TRequest, TResponse> {
    CompletionStage<TResponse> invoke(TRequest request);
}
