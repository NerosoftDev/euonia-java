package com.euonia.pipeline;

import java.util.concurrent.CompletionStage;

public interface PipelineBehavior {
    CompletionStage<Void> handleAsync(Object context, PipelineDelegate next);
}
