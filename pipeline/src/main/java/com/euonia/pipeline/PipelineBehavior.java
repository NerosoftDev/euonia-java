package com.euonia.pipeline;

import java.util.concurrent.CompletionStage;

public interface PipelineBehavior<C, R> {
    CompletionStage<R> handleAsync(C context, PipelineDelegate<C, R> next);
}
