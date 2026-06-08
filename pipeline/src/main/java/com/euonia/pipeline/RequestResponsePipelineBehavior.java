package com.euonia.pipeline;

import java.util.concurrent.CompletionStage;

public interface RequestResponsePipelineBehavior<TRequest, TResponse> {
    CompletionStage<TResponse> handleAsync(TRequest context, RequestResponsePipelineDelegate<TRequest, TResponse> next);
}
