package com.euonia.pipeline;

/**
 * PipelineFactory is responsible for creating instances of Pipeline.
 * It abstracts the creation logic and allows for different implementations of
 * Pipeline to be used without changing the client code that depends on it.
 */
public interface PipelineFactory {
    /**
     * Creates a new instance of Pipeline with the specified context and response
     * types.
     *
     * @param <C> the type of the context
     * @param <R> the type of the response
     * @return a new instance of Pipeline
     */
    <C, R> Pipeline<C, R> create();
}
