package com.euonia.pipeline;

import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Pipeline interface defines the contract for building and executing a pipeline
 * of components that can process a context object.
 * It allows adding components to the pipeline, building the pipeline, and
 * running it asynchronously with a given context.
 * The components can be added in a specific order or based on their type, and
 * the pipeline can be executed with an optional accumulation function to
 * combine results from multiple components.
 */
public interface Pipeline {
    /**
     * Adds a component to the pipeline. The component is defined as a function that
     * takes a PipelineDelegate and returns a PipelineDelegate.
     *
     * @param component The component to add to the pipeline.
     * @return The current pipeline instance.
     */
    Pipeline use(Function<PipelineDelegate, PipelineDelegate> component);

    /**
     * Adds a component to the pipeline at a specific index. The component is defined as a function that
     * takes a PipelineDelegate and returns a PipelineDelegate.
     *
     * @param component The component to add to the pipeline.
     * @param index The index at which to add the component.
     * @return The current pipeline instance.
     */
    Pipeline use(Function<PipelineDelegate, PipelineDelegate> component, int index);

    /**
     * Adds a component to the pipeline. The component is defined as a BiFunction that
     * takes a context object and a PipelineDelegate, and returns a CompletionStage<Void>.
     *
     * @param handler The handler to add to the pipeline.
     * @return The current pipeline instance.
     */
    Pipeline use(BiFunction<Object, PipelineDelegate, CompletionStage<Void>> handler);

    /**
     * Adds a component to the pipeline based on its type. The component is defined as a class and optional arguments.
     *
     * @param type The class type of the component to add.
     * @param args Optional arguments for the component's constructor.
     * @return The current pipeline instance.
     */
    Pipeline use(Class<?> type, Object... args);

    /**
     * Adds a component to the pipeline based on its context type. The component can be added ahead of others.
     *
     * @param contextType The context type of the component.
     * @param useAheadOfOthers Whether to add the component ahead of others.
     * @return The current pipeline instance.
     */
    Pipeline useOf(Class<?> contextType, boolean useAheadOfOthers);

    /**
     * Builds the pipeline and returns a PipelineDelegate.
     *
     * @return The built PipelineDelegate.
     */
    PipelineDelegate build();

    /**
     * Runs the pipeline asynchronously with the given context.
     *
     * @param context The context object to pass through the pipeline.
     * @return A CompletionStage representing the asynchronous execution.
     */
    CompletionStage<Void> runAsync(Object context);

    /**
     * Runs the pipeline asynchronously with the given context and an accumulation function.
     *
     * @param context The context object to pass through the pipeline.
     * @param accumulate The function to accumulate results from multiple components.
     * @return A CompletionStage representing the asynchronous execution.
     */
    CompletionStage<Void> runAsync(Object context, Function<Object, CompletionStage<Void>> accumulate);
}
