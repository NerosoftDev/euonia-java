package com.euonia.pipeline;

import java.util.concurrent.CompletionStage;

@FunctionalInterface
public interface PipelineDelegate<C, R> {
    CompletionStage<R> invoke(C context);
}
