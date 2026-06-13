package com.euonia.pipeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class PipelineBase<C, R> implements Pipeline<C, R> {
    private final List<Function<PipelineDelegate<C, R>, PipelineDelegate<C, R>>> components = new ArrayList<>();

    public List<Function<PipelineDelegate<C, R>, PipelineDelegate<C, R>>> getComponents() {
        return List.copyOf(components);
    }

    @Override
    public Pipeline<C, R> use(Function<PipelineDelegate<C, R>, PipelineDelegate<C, R>> component) {
        components.add(component);
        return this;
    }

    @Override
    public Pipeline<C, R> use(Function<PipelineDelegate<C, R>, PipelineDelegate<C, R>> component, int index) {
        components.add(index, component);
        return this;
    }

    @Override
    public Pipeline<C, R> use(BiFunction<C, PipelineDelegate<C, R>, CompletionStage<R>> handler) {
        return use(next -> request -> handler.apply(request, next));
    }

    @Override
    public Pipeline<C, R> use(Class<?> type, Object... args) {
        return use(next -> getNext(next, type, args));
    }

    @Override
    public Pipeline<C, R> useOf(Class<?> contextType, boolean useAheadOfOthers) {
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
    @SuppressWarnings("unchecked")
    public PipelineDelegate<C, R> build() {
        try {
            PipelineDelegate<C, R> app = request -> CompletableFuture.completedFuture((R) null);
            List<Function<PipelineDelegate<C, R>, PipelineDelegate<C, R>>> reversed = new ArrayList<>(components);
            Collections.reverse(reversed);
            for (Function<PipelineDelegate<C, R>, PipelineDelegate<C, R>> component : reversed) {
                app = component.apply(app);
            }
            return app;
        } finally {
            components.clear();
        }
    }

    @Override
    public CompletionStage<R> runAsync(C request) {
        useOf(request.getClass(), true);
        return build().invoke(request);
    }

    @Override
    public CompletionStage<R> runAsync(C request, Function<C, CompletionStage<R>> accumulate) {
        use((req, next) -> accumulate.apply(req));
        return runAsync(request);
    }

    protected abstract PipelineDelegate<C, R> getNext(PipelineDelegate<C, R> next, Class<?> type, Object... constructorArguments);
}
