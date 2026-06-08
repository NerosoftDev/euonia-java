package com.euonia.uow;

import java.lang.reflect.Method;

import com.euonia.uow.annotation.UnitOfWork;

public final class UnitOfWorkHelper {
    private UnitOfWorkHelper() {
    }

    public static boolean isUnitOfWorkType(Class<?> implementationType) {
        if (implementationType == null) {
            throw new IllegalArgumentException("implementationType");
        }

        if (hasUnitOfWorkAnnotation(implementationType) || anyMethodHasUnitOfWorkAnnotation(implementationType)) {
            return true;
        }

        return UnitOfWorkEnabled.class.isAssignableFrom(implementationType);
    }

    public static boolean isUnitOfWorkMethod(Method method) {
        return getUnitOfWorkAnnotation(method) != null;
    }

    public static UnitOfWork getUnitOfWorkAnnotation(Method method) {
        if (method == null) {
            throw new IllegalArgumentException("method");
        }

        UnitOfWork annotation = method.getAnnotation(UnitOfWork.class);
        if (annotation != null) {
            return annotation.disabled() ? null : annotation;
        }

        Class<?> declaringClass = method.getDeclaringClass();
        annotation = declaringClass.getAnnotation(UnitOfWork.class);
        if (annotation != null) {
            return annotation.disabled() ? null : annotation;
        }

        return null;
    }

    private static boolean anyMethodHasUnitOfWorkAnnotation(Class<?> implementationType) {
        for (Method method : implementationType.getDeclaredMethods()) {
            if (method.isAnnotationPresent(UnitOfWork.class)) {
                return true;
            }
        }

        return false;
    }

    private static boolean hasUnitOfWorkAnnotation(Class<?> implementationType) {
        UnitOfWork annotation = implementationType.getAnnotation(UnitOfWork.class);
        return annotation != null && !annotation.disabled();
    }
}
