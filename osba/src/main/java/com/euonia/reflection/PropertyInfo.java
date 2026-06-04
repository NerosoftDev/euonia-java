package com.euonia.reflection;

import com.euonia.osba.BusinessObject;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Represents metadata about a property of a business object, including its name, type, default value, and display name.
 *
 * @param <T> the type of the property value
 */
public class PropertyInfo<T> implements Comparable<PropertyInfo<T>> {
    private final Class<T> type;
    private final String name;
    private String friendlyName;
    private T defaultValue;
    private Field field;

    @SuppressWarnings("unchecked")
    public PropertyInfo(String name) {
        this.name = name;
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType parameterizedType) {
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            this.type = (Class<T>) actualTypeArguments[0];
        } else {
            throw new IllegalArgumentException("Unable to determine generic type T for PropertyInfoImpl");
        }
    }

    public PropertyInfo(String name, String friendlyName, T defaultValue) {
        this(name);
        this.friendlyName = friendlyName;
        this.defaultValue = defaultValue;
    }

    public PropertyInfo(String name, String friendlyName, Class<?> objectType, T defaultValue) {
        this(name, friendlyName, defaultValue);
        try {
            this.field = objectType.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            //
        }
    }

    public String getName() {
        return name;
    }

    public String getFriendlyName() {
        if (!Objects.isNull(friendlyName)) {
            return friendlyName;
        }

        if (field != null) {
            var annotation = field.getAnnotation(DisplayName.class);
            if (annotation != null) {
                return annotation.value();
            }
        }
        return name;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public Class<T> getType() {
        return type;
    }

    public boolean isChild() {
        return type.isAssignableFrom(BusinessObject.class);
    }

    public Field getField() {
        return field;
    }

    public FieldData<?> newFieldData(String name) {
        return new FieldData<T>(name);
    }

    @Override
    public int compareTo(PropertyInfo o) {
        return this.name.compareTo(o.getName());
    }
}
