package com.euonia.reflection;

import java.util.Stack;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

import com.euonia.osba.abstracts.TrackableObject;

/**
 * Represents a field of an object that can be tracked for changes.
 * This class provides methods to get and set the value of the field, as well as to mark it as unchanged or undo changes.
 *
 * @param <T> the type of the field value.
 */
public class FieldData<T> implements TrackableObject {
    private final Stack<T> history = new Stack<>();
    private final Flow.Publisher<T> publisher = new SubmissionPublisher<>();

    private String name;
    private T value;

    public FieldData() {
        Flow.Subscriber<T> subscriber = new Flow.Subscriber<>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(T item) {
                history.push(item);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        };
        publisher.subscribe(subscriber);
    }

    public FieldData(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        if (value == null || !value.getClass().isAssignableFrom(this.value.getClass())) {
            throw new IllegalArgumentException("Value must be of type " + this.value.getClass().getName());
        }
        if (this.value == value) {
            return;
        }
        this.value = (T) value;
        ((SubmissionPublisher<T>) publisher).submit(this.value);
    }

    public void markAsUnchanged() {
        history.clear();
    }

    public void undo() {
        if (!history.isEmpty()) {
            this.value = history.pop();
        }
    }

    @Override
    public boolean isChanged() {
        return !history.isEmpty();
    }

    @Override
    public boolean isDeleted() {
        if (value instanceof TrackableObject trackableObject) {
            return trackableObject.isDeleted();
        }
        return false;
    }

    @Override
    public boolean isNew() {
        if (value instanceof TrackableObject trackableObject) {
            return trackableObject.isNew();
        }
        return false;
    }

    @Override
    public boolean isSavable() {
        if (value instanceof TrackableObject trackableObject) {
            return trackableObject.isSavable();
        }
        return false;
    }

    @Override
    public boolean isValid() {
        if (value instanceof TrackableObject trackableObject) {
            return trackableObject.isValid();
        }
        return false;
    }
}
