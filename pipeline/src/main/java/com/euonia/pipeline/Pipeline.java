package com.euonia.pipeline;

import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Pipeline interface defines the contract for building and executing a pipeline
 * of components that can process a request and produce a response.
 * It allows adding components to the pipeline, building the pipeline, and
 * running it asynchronously with a given request.
 * The components can be added in a specific order or based on their type, and
 * the pipeline can be executed with an optional accumulation function to
 * combine results from multiple components.
 *
 * @param <C> the type of the request/context object
 * @param <R> the type of the response object
 */
public interface Pipeline<C, R> {
    /**
     * Adds a component to the pipeline. The component is defined as a function that
     * takes a PipelineDelegate and returns a PipelineDelegate.
     *
     * @param component The component to add to the pipeline.
     * @return The current pipeline instance.
     */
    Pipeline<C, R> use(Function<PipelineDelegate<C, R>, PipelineDelegate<C, R>> component);

    /**
     * Adds a component to the pipeline at a specific index. The component is
     * defined as a function that
     * takes a PipelineDelegate and returns a PipelineDelegate.
     *
     * @param component The component to add to the pipeline.
     * @param index     The index at which to add the component.
     * @return The current pipeline instance.
     */
    Pipeline<C, R> use(Function<PipelineDelegate<C, R>, PipelineDelegate<C, R>> component, int index);

    /**
     * Adds a component to the pipeline. The component is defined as a BiFunction
     * that
     * takes a request object and a PipelineDelegate, and returns a CompletionStage
     * of the response.
     *
     * @param handler The handler to add to the pipeline.
     * @return The current pipeline instance.
     */
    Pipeline<C, R> use(BiFunction<C, PipelineDelegate<C, R>, CompletionStage<R>> handler);

    /**
     * Adds a component to the pipeline based on its type. The component is defined
     * as a class and optional arguments.
     *
     * @param type The class type of the component to add.
     * @param args Optional arguments for the component's constructor.
     * @return The current pipeline instance.
     */
    Pipeline<C, R> use(Class<?> type, Object... args);

    /**
     * Adds a component to the pipeline based on its context type. The component can
     * be added ahead of others.
     *
     * @param contextType      The context type of the component.
     * @param useAheadOfOthers Whether to add the component ahead of others.
     * @return The current pipeline instance.
     */
    Pipeline<C, R> useOf(Class<?> contextType, boolean useAheadOfOthers);

    /**
     * Builds the pipeline and returns a PipelineDelegate.
     *
     * @return The built PipelineDelegate.
     */
    PipelineDelegate<C, R> build();

    /**
     * Runs the pipeline asynchronously with the given request.
     *
     * @param request The request object to pass through the pipeline.
     * @return A CompletionStage representing the asynchronous execution result.
     */
    CompletionStage<R> runAsync(C request);

    /**
     * Runs the pipeline asynchronously with the given request and an accumulation
     * function.
     *
     * @param request    The request object to pass through the pipeline.
     * @param accumulate The function to accumulate results from multiple
     *                   components.
     * @return A CompletionStage representing the asynchronous execution result.
     */
    CompletionStage<R> runAsync(C request, Function<C, CompletionStage<R>> accumulate);
}
