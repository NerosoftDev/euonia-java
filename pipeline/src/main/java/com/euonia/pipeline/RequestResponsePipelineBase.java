package com.euonia.pipeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class RequestResponsePipelineBase<TRequest, TResponse> implements RequestResponsePipeline<TRequest, TResponse> {
    private final List<Function<RequestResponsePipelineDelegate<TRequest, TResponse>, RequestResponsePipelineDelegate<TRequest, TResponse>>> components = new ArrayList<>();

    public List<Function<RequestResponsePipelineDelegate<TRequest, TResponse>, RequestResponsePipelineDelegate<TRequest, TResponse>>> getComponents() {
        return List.copyOf(components);
    }

    @Override
    public RequestResponsePipeline<TRequest, TResponse> use(Function<RequestResponsePipelineDelegate<TRequest, TResponse>, RequestResponsePipelineDelegate<TRequest, TResponse>> component) {
        components.add(component);
        return this;
    }

    @Override
    public RequestResponsePipeline<TRequest, TResponse> use(Function<RequestResponsePipelineDelegate<TRequest, TResponse>, RequestResponsePipelineDelegate<TRequest, TResponse>> component,
                                                             int index) {
        components.add(index, component);
        return this;
    }

    @Override
    public RequestResponsePipeline<TRequest, TResponse> use(BiFunction<TRequest, RequestResponsePipelineDelegate<TRequest, TResponse>, CompletionStage<TResponse>> handler) {
        return use(next -> request -> handler.apply(request, next));
    }

    @Override
    public RequestResponsePipeline<TRequest, TResponse> use(Class<?> type, Object... args) {
        return use(next -> getNext(next, type, args));
    }

    @Override
    public RequestResponsePipeline<TRequest, TResponse> useOf(Class<?> contextType, boolean useAheadOfOthers) {
        PipelineBehaviors annotation = contextType.getAnnotation(PipelineBehaviors.class);
        if (annotation == null) {
            return this;
        }
        if (useAheadOfOthers) {
            Class<?>[] behaviorTypes = annotation.value();
            for (int index = 0; index < behaviorTypes.length; index++) {
                Class<?> behaviorType = behaviorTypes[index];
                use(next -> getNext(next, behaviorType), index);
            }
            return this;
        }

        for (Class<?> behaviorType : annotation.value()) {
            use(behaviorType);
        }
        return this;
    }

    @Override
    public RequestResponsePipelineDelegate<TRequest, TResponse> build() {
        try {
            RequestResponsePipelineDelegate<TRequest, TResponse> app = request -> {
                throw new IllegalStateException("Pipeline terminal handler is not configured.");
            };
            List<Function<RequestResponsePipelineDelegate<TRequest, TResponse>, RequestResponsePipelineDelegate<TRequest, TResponse>>> reversed = new ArrayList<>(components);
            Collections.reverse(reversed);
            for (Function<RequestResponsePipelineDelegate<TRequest, TResponse>, RequestResponsePipelineDelegate<TRequest, TResponse>> component : reversed) {
                app = component.apply(app);
            }
            return app;
        } finally {
            components.clear();
        }
    }

    @Override
    public CompletionStage<TResponse> runAsync(TRequest context) {
        useOf(context.getClass(), true);
        return build().invoke(context);
    }

    @Override
    public CompletionStage<TResponse> runAsync(TRequest context, Function<TRequest, CompletionStage<TResponse>> accumulate) {
        use((request, next) -> accumulate.apply(request));
        return runAsync(context);
    }

    protected abstract RequestResponsePipelineDelegate<TRequest, TResponse> getNext(RequestResponsePipelineDelegate<TRequest, TResponse> next,
                                                                                     Class<?> type,
                                                                                     Object... constructorArguments);
}
