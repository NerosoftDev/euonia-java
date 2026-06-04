package com.euonia.core;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class Singleton {
    private static final ConcurrentHashMap<Class<?>, Object> instances = new ConcurrentHashMap<>();

    private Singleton() {
        // private constructor to prevent instantiation
    }

    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<T> clazz) {
        return (T) instances.computeIfAbsent(clazz, Singleton::createInstance);
    }

    private static <T> T createInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException
                | InvocationTargetException e) {
            throw new RuntimeException("Failed to create instance of " + clazz.getName(), e);
        }
    }

    public static <T> T get(Class<T> clazz, Supplier<T> supplier) {
        return (T) instances.computeIfAbsent(clazz, k -> supplier.get());
    }
}
