package com.euonia.usecase;

/**
 * Represents a use case in the application. A use case is a specific operation or action that can be performed within the system.
 * It defines a contract for executing a particular business logic or functionality.
 *
 * @param <I> the type of the input to the use case
 * @param <O> the type of the output of the use case
 */
public interface UseCase<I, O> {
    /**
     * Executes the use case with the given input and returns the output.
     *
     * @param input the input data for the use case
     * @return the output data from the use case
     */
    O execute(I input);
}
