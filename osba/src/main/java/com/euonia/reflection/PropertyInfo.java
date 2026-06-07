package com.euonia.reflection;

import com.euonia.osba.BusinessObject;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Represents metadata about a property of a business object, including its name, type, default value, and display name.
 *
 * @param <T> the type of the property value
 */
public class PropertyInfo<T> implements Comparable<PropertyInfo<T>> {
    private final Class<T> type;
    private final String name;
    private String friendlyName;
    private Supplier<T> defaultValue;
    private Field field;

    public PropertyInfo(Class<T> type, String name) {
        this.name = name;
        this.type = type;
    }

    public PropertyInfo(Class<T> type, String name, String friendlyName, Supplier<T> defaultValue) {
        this(type, name);
        this.friendlyName = friendlyName;
        this.defaultValue = defaultValue;
    }

    public PropertyInfo(Class<T> type, String name, String friendlyName, Class<?> objectType, Supplier<T> defaultValue) {
        this(type, name, friendlyName, defaultValue);
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
        return defaultValue == null ? null : defaultValue.get();
    }

    public Class<T> getType() {
        return type;
    }

    public boolean isChild() {
        return BusinessObject.class.isAssignableFrom(type);
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
