package com.euonia.usecase;

/**
 * Represents a successful output of a use case execution.
 * This interface defines a method to handle the result of a successful use case execution.
 *
 * @param <O> the type of the successful output
 */
public interface UseCaseSuccess<O> {
    /**
     * Indicates that the use case execution has succeeded with a result.
     *
     * @param output the result of the successful execution
     */
    void success(O output);
}
