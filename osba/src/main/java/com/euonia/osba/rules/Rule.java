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

    CompletableFuture<Void> executeAsync(RuleContext context);
}
