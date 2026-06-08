package com.euonia.pipeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class PipelineBase implements Pipeline {
    private final List<Function<PipelineDelegate, PipelineDelegate>> components = new ArrayList<>();

    public List<Function<PipelineDelegate, PipelineDelegate>> getComponents() {
        return List.copyOf(components);
    }

    @Override
    public Pipeline use(Function<PipelineDelegate, PipelineDelegate> component) {
        components.add(component);
        return this;
    }

    @Override
    public Pipeline use(Function<PipelineDelegate, PipelineDelegate> component, int index) {
        components.add(index, component);
        return this;
    }

    @Override
    public Pipeline use(BiFunction<Object, PipelineDelegate, CompletionStage<Void>> handler) {
        return use(next -> context -> handler.apply(context, next));
    }

    @Override
    public Pipeline use(Class<?> type, Object... args) {
        return use(next -> getNext(next, type, args));
    }

    @Override
    public Pipeline useOf(Class<?> contextType, boolean useAheadOfOthers) {
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
    public PipelineDelegate build() {
        try {
            PipelineDelegate app = context -> CompletableFuture.completedFuture(null);
            List<Function<PipelineDelegate, PipelineDelegate>> reversed = new ArrayList<>(components);
            Collections.reverse(reversed);
            for (Function<PipelineDelegate, PipelineDelegate> component : reversed) {
                app = component.apply(app);
            }
            return app;
        } finally {
            components.clear();
        }
    }

    @Override
    public CompletionStage<Void> runAsync(Object context) {
        useOf(context.getClass(), true);
        return build().invoke(context);
    }

    @Override
    public CompletionStage<Void> runAsync(Object context, Function<Object, CompletionStage<Void>> accumulate) {
        use((request, next) -> accumulate.apply(request));
        return runAsync(context);
    }

    protected abstract PipelineDelegate getNext(PipelineDelegate next, Class<?> type, Object... constructorArguments);
}
