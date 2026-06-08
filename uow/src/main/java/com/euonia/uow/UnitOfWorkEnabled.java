package com.euonia.uow;

/**
 * Marker interface indicating that a type requires a unit of work.
 *
 * <p>Implement this interface on any service or handler class whose
 * public methods should be automatically intercepted with a unit of work
 * when used with a unit-of-work-aware proxy or AOP aspect.</p>
 *
 * @see UnitOfWork
 */
public interface UnitOfWorkEnabled {
}
