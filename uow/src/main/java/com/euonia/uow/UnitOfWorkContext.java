package com.euonia.uow;

import java.util.concurrent.CompletionStage;

public interface UnitOfWorkContext {
    CompletionStage<Void> saveChangesAsync();

    CompletionStage<Void> commitAsync();

    CompletionStage<Void> rollbackAsync();

    default void close() {
    }
}
