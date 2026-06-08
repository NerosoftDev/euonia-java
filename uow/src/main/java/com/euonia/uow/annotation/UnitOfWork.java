package com.euonia.uow.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a type or method as requiring a unit of work.
 *
 * <p>When applied to a class, all methods are wrapped in a unit of work.
 * When applied to a method, only that specific method is wrapped.
 *
 * <pre>{@code
 * @UnitOfWork
 * public class OrderService { ... }
 *
 * @UnitOfWork(disabled = true)
 * public void readOnlyQuery() { ... }
 * }</pre>
 *
 * @see com.euonia.uow.UnitOfWork
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UnitOfWork {
    /**
     * When {@code true}, the unit of work is suppressed for the annotated
     * element even if a parent scope declares it.
     *
     * @return whether the unit of work is disabled
     */
    boolean disabled() default false;
}
