package com.euonia.usecase;

import java.util.concurrent.Flow.*;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Consumer;

/**
 * A presenter that implements the output ports for a use case and allows subscribers to listen for success and failure events.
 *
 * @param <O> the type of the successful output of the use case
 */
public class UseCasePresenter<O> implements UseCaseSuccess<O>, UseCaseFailure, AutoCloseable {
    private final Publisher<O> publisher = new SubmissionPublisher<>();

    private O output;

    /**
     * Subscribes to the presenter to receive success and failure events.
     *
     * @param onSuccess the consumer to handle successful output
     * @param onFailure the consumer to handle errors
     */
    public void subscribe(Consumer<O> onSuccess, Consumer<Throwable> onFailure) {
        publisher.subscribe(new Subscriber<>() {

            @Override
            public void onSubscribe(Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(O item) {
                onSuccess.accept(item);
            }

            @Override
            public void onError(Throwable throwable) {
                onFailure.accept(throwable);
            }

            @Override
            public void onComplete() {
                // Handle completion if needed
            }
        });
    }

    @Override
    public void success(O output) {
        this.output = output;
        ((SubmissionPublisher<O>) publisher).submit(output);
    }

    @Override
    public void error(Throwable throwable) {
        ((SubmissionPublisher<O>) publisher).closeExceptionally(throwable);
    }

    @Override
    public void close() throws Exception {
        ((SubmissionPublisher<O>) publisher).close();
    }

    /**
     * Returns the output of the use case execution if it was successful.
     *
     * @return the output of the use case execution
     */
    public O getOutput() {
        return output;
    }
}
