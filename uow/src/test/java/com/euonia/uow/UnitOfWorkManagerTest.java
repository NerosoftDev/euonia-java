package com.euonia.uow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;

class UnitOfWorkManagerTest {
    @Test
    void beginShouldSetAndRestoreCurrentUnitOfWork() {
        UnitOfWorkAccessor accessor = new UnitOfWorkAccessor();
        UnitOfWorkManager manager = new UnitOfWorkManager(accessor, new UnitOfWorkOptions());

        try (UnitOfWork unitOfWork = manager.begin(new UnitOfWorkOptions(true), true)) {
            assertSame(unitOfWork, manager.getCurrent());
        }

        assertSame(null, manager.getCurrent());
    }

    @Test
    void beginWithoutRequiresNewShouldReturnChildWrapper() {
        UnitOfWorkAccessor accessor = new UnitOfWorkAccessor();
        UnitOfWorkManager manager = new UnitOfWorkManager(accessor, new UnitOfWorkOptions());

        try (UnitOfWork parent = manager.begin(new UnitOfWorkOptions(true), true)) {
            UnitOfWork child = manager.begin(new UnitOfWorkOptions(false), false);

            assertInstanceOf(ChildUnitOfWork.class, child);
            assertEquals(parent.getItems(), child.getItems());
            assertEquals(parent.getContexts(), child.getContexts());
        }
    }
}
