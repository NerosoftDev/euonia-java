package com.euonia.pipeline;

import java.util.concurrent.CompletionStage;

@FunctionalInterface
public interface PipelineDelegate {
    CompletionStage<Void> invoke(Object context);
}
