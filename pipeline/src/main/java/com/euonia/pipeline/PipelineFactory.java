package com.euonia.pipeline;

public interface PipelineFactory {
    Pipeline create();

    <TRequest, TResponse> RequestResponsePipeline<TRequest, TResponse> createRequestResponse();
}
