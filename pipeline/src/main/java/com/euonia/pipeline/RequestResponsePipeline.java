package com.euonia.pipeline;

import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface RequestResponsePipeline<TRequest, TResponse> {
    RequestResponsePipeline<TRequest, TResponse> use(Function<RequestResponsePipelineDelegate<TRequest, TResponse>, RequestResponsePipelineDelegate<TRequest, TResponse>> component);

    RequestResponsePipeline<TRequest, TResponse> use(Function<RequestResponsePipelineDelegate<TRequest, TResponse>, RequestResponsePipelineDelegate<TRequest, TResponse>> component,
                                                      int index);

    RequestResponsePipeline<TRequest, TResponse> use(BiFunction<TRequest, RequestResponsePipelineDelegate<TRequest, TResponse>, CompletionStage<TResponse>> handler);

    RequestResponsePipeline<TRequest, TResponse> use(Class<?> type, Object... args);

    RequestResponsePipeline<TRequest, TResponse> useOf(Class<?> contextType, boolean useAheadOfOthers);

    RequestResponsePipelineDelegate<TRequest, TResponse> build();

    CompletionStage<TResponse> runAsync(TRequest context);

    CompletionStage<TResponse> runAsync(TRequest context, Function<TRequest, CompletionStage<TResponse>> accumulate);
}
