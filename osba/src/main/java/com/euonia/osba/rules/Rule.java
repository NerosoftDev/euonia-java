package com.euonia.osba.rules;

import com.euonia.reflection.PropertyInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Rule {
    String getName();

    PropertyInfo<?> getProperty();

    default List<PropertyInfo<?>> getRelatedProperties() {
        return List.of();
    }

    int getPriority();

    void setPriority(int priority);

    CompletableFuture<Void> executeAsync(RuleContext context);
}
