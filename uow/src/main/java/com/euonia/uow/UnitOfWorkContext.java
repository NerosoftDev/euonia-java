package com.euonia.uow;

import java.util.concurrent.CompletionStage;

/**
 * Abstraction for a transactional resource (database, message broker, etc.)
 * that participates in a unit of work.
 *
 * <p>Implementations register themselves with a {@link UnitOfWork} via
 * {@link UnitOfWork#addContext} and receive lifecycle callbacks:</p>
 * <ol>
 *   <li>{@link #saveChangesAsync()} — flush pending changes</li>
 *   <li>{@link #commitAsync()} — commit the transaction</li>
 *   <li>{@link #rollbackAsync()} — roll back the transaction</li>
 *   <li>{@link #close()} — release resources</li>
 * </ol>
 *
 * @see UnitOfWork
 */
public interface UnitOfWorkContext {
    /**
     * Persists pending changes to the underlying resource without
     * committing the transaction.
     *
     * @return a stage that completes when changes are saved
     */
    CompletionStage<Void> saveChangesAsync();

    /**
     * Commits the transaction.
     *
     * @return a stage that completes when the commit is done
     */
    CompletionStage<Void> commitAsync();

    /**
     * Rolls back the transaction.
     *
     * @return a stage that completes when the rollback is done
     */
    CompletionStage<Void> rollbackAsync();

    /**
     * Releases any resources held by this context.
     * The default implementation does nothing.
     */
    default void close() {
    }
}
