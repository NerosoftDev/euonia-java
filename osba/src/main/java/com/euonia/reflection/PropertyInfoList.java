package com.euonia.reflection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class PropertyInfoList extends ArrayList<PropertyInfo<?>> {
    private final Semaphore semaphore = new Semaphore(1);

    public PropertyInfoList() {
        super();
    }

    public PropertyInfoList(List<PropertyInfo<?>> list) {
        super(list);
    }

    private boolean locked = false;

    public boolean isLocked() {
        return locked;
    }

    public void lock() {
        locked = true;
    }

    public void unlock() {
        locked = false;
    }

    public PropertyInfo<?> getOrAdd(PropertyInfo<?> propertyInfo) {
        try {
            semaphore.acquire();
            var existing = this.stream().filter(p -> p.getName().equals(propertyInfo.getName())).findFirst().orElse(null);
            if (existing != null) {
                return existing;
            }
            this.add(propertyInfo);
            return propertyInfo;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted while trying to acquire semaphore.", e);
        } finally {
            semaphore.release();
        }
    }
}
