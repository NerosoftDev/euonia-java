package com.euonia.osba;

import com.euonia.osba.abstracts.Savable;
import com.euonia.osba.rules.BrokenRule;
import com.euonia.osba.rules.RuleCheckException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow.*;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class EditableObject<T extends EditableObject<T>> extends ObservableObject<T> implements Savable<T> {

    private final Publisher<SavedEventArgs> savedEventPublisher = new SubmissionPublisher<>();

    public final void onSaved(Consumer<SavedEventArgs> listener) {
        if (listener != null) {
            savedEventPublisher.subscribe(new Subscriber<>() {
                @Override
                public void onSubscribe(Subscription subscription) {
                    subscription.request(Long.MAX_VALUE);
                }

                @Override
                public void onNext(SavedEventArgs item) {
                    listener.accept(item);
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onComplete() {

                }
            });
        }
    }

    protected void onSaved(T newObject, Throwable error, Object userState) {
        ((SubmissionPublisher<SavedEventArgs>) savedEventPublisher).submit(new SavedEventArgs(newObject, error, userState));
    }

    @Override
    public void saveComplete(T newObject) {
        onSaved(newObject, null, null);
    }

    @Override
    public T save(boolean forceUpdate) {
        return save(forceUpdate, null);
    }

    /**
     * Saves the current object to the database or other persistent storage.
     * If forceUpdate is true, the object will be saved even if it has not been marked as changed.
     * The userState parameter can be used to pass additional information to the onSaved event.
     *
     * @param forceUpdate whether to force the update even if the object has not been marked as changed
     * @param userState   additional information to pass to the onSaved event
     * @return the saved object
     */
    @SuppressWarnings({"SameParameterValue", "unchecked"})
    protected T save(boolean forceUpdate, Object userState) {
        if (getState() == ObjectEditState.NONE) {
            if (forceUpdate) {
                markAsChanged();
            } else {
                return (T) this;
            }
        }

        CompletableFuture<List<String>> validations;

        if (!isDeleted() || isCheckObjectRulesOnDelete()) {
//            validations = getRules().checkObjectRulesAsync()
//                                    .exceptionally(error -> {
//                                        onSaved(null, error, userState);
//                                        return null;
//                                    });
            validations = getRules().checkObjectRulesAsync();
        } else {
            validations = CompletableFuture.completedFuture(List.of());
        }

        return validations.thenCompose(ignored -> {
            if (!isValid() && (!isDeleted() || isCheckObjectRulesOnDelete())) {
                var errors = getRules().getBrokenRules()
                                       .stream()
                                       .collect(Collectors.groupingBy(BrokenRule::getProperty, Collectors.mapping(BrokenRule::getDescription, Collectors.toList())));
                return CompletableFuture.failedFuture(new RuleCheckException(errors));
            }

            return CompletableFuture.supplyAsync(() -> {
                try {
                    markAsBusy();
                    T result = getBusinessContext().getObjectFactory().save((Class<T>) getClass(), (T) this);
                    onSaved(result, null, userState);
                    return result;
                } catch (Throwable error) {
                    onSaved(null, error, userState);
                    throw new RuntimeException(error);
                } finally {
                    markAsIdle();
                }
            });
        }).join();
    }

    protected void create() {
    }

    protected void insert() {
    }

    protected void update() {
    }

    protected void delete() {
    }
}
