package com.euonia.uow;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class UnitOfWorkTest {
    @Test
    void addAndFindContextShouldWork() {
        try (UnitOfWork unitOfWork = new UnitOfWork(new UnitOfWorkOptions(false, UnitOfWorkIsolationLevel.READ_COMMITTED, Duration.ofSeconds(1)))) {
            unitOfWork.initialize(new UnitOfWorkOptions(true));

            TestContext context = new TestContext();
            unitOfWork.addContext("primary", context);

            assertEquals(context, unitOfWork.findContext("primary"));
            assertEquals(context, unitOfWork.getOrAddContext("primary", TestContext::new));
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                unitOfWork.addContext("primary", new TestContext());
            });
            assertEquals("This unit of work already contains a context with the key: primary", exception.getMessage());
        }
    }

    @Test
    void completeShouldInvokeHandlersAndListeners() {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            unitOfWork.initialize(new UnitOfWorkOptions(true));

            AtomicBoolean completed = new AtomicBoolean(false);
            AtomicBoolean handlerCalled = new AtomicBoolean(false);
            unitOfWork.addCompletedListener(event -> completed.set(event.getUnitOfWork() == unitOfWork));
            unitOfWork.onCompleted(() -> {
                handlerCalled.set(true);
                return CompletableFuture.completedFuture(null);
            });

            unitOfWork.completeAsync().toCompletableFuture().join();

            assertTrue(unitOfWork.isCompleted());
            assertTrue(completed.get());
            assertTrue(handlerCalled.get());
        }
    }

    @Test
    void rollbackShouldPreventCompletion() {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            unitOfWork.initialize(new UnitOfWorkOptions(true));

            unitOfWork.rollbackAsync().toCompletableFuture().join();
            unitOfWork.completeAsync().toCompletableFuture().join();

            assertFalse(unitOfWork.isCompleted());
        }
    }

    @Test
    void closeShouldEmitFailureWhenNotCompleted() {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            unitOfWork.initialize(new UnitOfWorkOptions(true));

            AtomicBoolean failed = new AtomicBoolean(false);
            AtomicBoolean disposed = new AtomicBoolean(false);
            unitOfWork.addFailedListener(event -> failed.set(event.getUnitOfWork() == unitOfWork));
            unitOfWork.addDisposedListener(event -> disposed.set(event.getUnitOfWork() == unitOfWork));

            unitOfWork.close();

            assertTrue(unitOfWork.isDisposed());
            assertTrue(failed.get());
            assertTrue(disposed.get());
        }
    }

    @Test
    void completeShouldFailWhenCalledTwice() {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            unitOfWork.initialize(new UnitOfWorkOptions(true));

            unitOfWork.completeAsync().toCompletableFuture().join();

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                unitOfWork.completeAsync().toCompletableFuture().join();
            });
            assertEquals("Completion has already been requested for this unit of work.", exception.getMessage());
        }
    }

    private static final class TestContext implements UnitOfWorkContext {
        @Override
        public CompletionStage<Void> saveChangesAsync() {
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletionStage<Void> commitAsync() {
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletionStage<Void> rollbackAsync() {
            return CompletableFuture.completedFuture(null);
        }
    }
}
