package com.euonia.usecase;

/**
 * Represents a failure output of a use case execution.
 * This interface defines a method to handle errors that occur during the execution of a use case.
 */
public interface UseCaseFailure {
    /**
     * Indicates that the use case execution has failed with an error.
     *
     * @param throwable the error that caused the failure
     */
    void error(Throwable throwable);
}
