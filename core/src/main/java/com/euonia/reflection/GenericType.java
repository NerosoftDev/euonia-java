package com.euonia.reflection;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class GenericType<T> {
    private final Type type;

    private GenericType(Type type) {
        this.type = type;
    }

    protected GenericType() {
        Class<?> parameterizedTypeReferenceSubclass = findGenericTypeReferenceSubclass(getClass());
        Type type = parameterizedTypeReferenceSubclass.getGenericSuperclass();
        if (!(type instanceof ParameterizedType parameterizedType)) {
            throw new IllegalArgumentException("Type must be a parameterized type");
        }
        //Assert.isInstanceOf(ParameterizedType.class, type, "Type must be a parameterized type");
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        if (actualTypeArguments.length != 1) {
            throw new IllegalArgumentException("Wrong number of actual type arguments");
        }
        //Assert.isTrue(actualTypeArguments.length == 1, "Number of type arguments must be 1");
        this.type = actualTypeArguments[0];
    }

    public Type getType() {
        return this.type;
    }

    public static <T> GenericType<T> forType(Type type) {
        return new GenericType<>(type) {
        };
    }

    @Override
    public boolean equals(Object other) {
        return (this == other || (other instanceof GenericType<?> that && this.type.equals(that.type)));
    }

    @Override
    public int hashCode() {
        return this.type.hashCode();
    }

    @Override
    public String toString() {
        return "GenericType<" + this.type + ">";
    }

    private static Class<?> findGenericTypeReferenceSubclass(Class<?> child) {
        Class<?> parent = child.getSuperclass();
        if (Object.class == parent) {
            throw new IllegalStateException("Expected GenericType superclass");
        } else if (GenericType.class == parent) {
            return child;
        } else {
            return findGenericTypeReferenceSubclass(parent);
        }
    }
}
