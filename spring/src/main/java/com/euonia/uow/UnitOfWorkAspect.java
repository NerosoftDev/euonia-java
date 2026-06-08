package com.euonia.uow;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * Spring AOP aspect that intercepts methods annotated with
 * {@link com.euonia.uow.annotation.UnitOfWork @UnitOfWork} and
 * wraps them in a unit of work.
 *
 * <h3>How it works</h3>
 * <ol>
 *   <li>Before the method executes, a new {@link UnitOfWork} is begun
 *       via {@link UnitOfWorkManager}.</li>
 *   <li>The method proceeds normally.</li>
 *   <li>On success, the unit of work is completed (save → handlers → listeners).</li>
 *   <li>On failure, the unit of work is rolled back and the exception is
 *       propagated. Failed listeners are fired via {@link UnitOfWork#close()}.</li>
 *   <li>The unit of work is always disposed in a {@code finally} block.</li>
 * </ol>
 *
 * <h3>Pointcut</h3>
 * <p>Intercepts any Spring-managed bean method whose class or method is
 * annotated with {@code @UnitOfWork}. Methods annotated with
 * {@code @UnitOfWork(disabled = true)} are skipped.</p>
 *
 * @see UnitOfWorkManager
 * @see com.euonia.uow.annotation.UnitOfWork
 */
@Aspect
public class UnitOfWorkAspect {

    private final UnitOfWorkManager unitOfWorkManager;

    /**
     * Creates an aspect that uses the given manager for unit-of-work lifecycle.
     *
     * @param unitOfWorkManager the unit-of-work manager
     */
    public UnitOfWorkAspect(UnitOfWorkManager unitOfWorkManager) {
        this.unitOfWorkManager = unitOfWorkManager;
    }

    /**
     * Around advice that wraps annotated methods in a unit of work.
     *
     * @param pjp the join point
     * @return the method's return value
     * @throws Throwable if the method throws
     */
    @Around("@within(com.euonia.uow.annotation.UnitOfWork) || @annotation(com.euonia.uow.annotation.UnitOfWork)")
    public Object aroundUnitOfWork(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        if (!UnitOfWorkHelper.isUnitOfWorkMethod(method)) {
            return pjp.proceed();
        }

        try (UnitOfWork uow = unitOfWorkManager.begin(new UnitOfWorkOptions(true), false)) {
            Object result = pjp.proceed();
            uow.completeAsync().toCompletableFuture().join();
            return result;
        } catch (Throwable t) {
            // close() (via try-with-resources) detects !completed
            // and fires failed listeners automatically
            throw t;
        }
    }
}
