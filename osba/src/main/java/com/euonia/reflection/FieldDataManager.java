package com.euonia.reflection;

import com.euonia.osba.BusinessObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class FieldDataManager {
    private final Map<String, FieldData<?>> fieldData = new HashMap<>();
    private final List<PropertyInfo<?>> properties;

    public FieldDataManager(Class<?> businessObjectType) {
        properties = createConsolidatedList(businessObjectType);
    }

    private static List<PropertyInfo<?>> createConsolidatedList(Class<?> type) {
        var result = new ArrayList<PropertyInfo<?>>();

        var currentType = type;
        var hierarchy = new ArrayList<Type>();
        do {
            hierarchy.add(currentType);
            currentType = currentType.getSuperclass();
        }
        while (currentType != null && !(BusinessObject.class.isAssignableFrom(currentType)));

        for (var index = hierarchy.size() - 1; index >= 0; index--) {
            var source = PropertyInfoManager.getPropertyListCache(hierarchy.get(index));
            source.lock();
            result.addAll(source);
        }

        return result;
    }

    public List<PropertyInfo<?>> getRegisteredProperties() {
        return properties;
    }

    public PropertyInfo<?> getRegisteredProperty(String propertyName) {
        return getRegisteredProperties().stream()
                                        .filter(p -> p.getName().equals(propertyName))
                                        .findFirst()
                                        .orElseThrow(() -> new IllegalArgumentException("Property '" + propertyName + "' is not registered."));
    }

    public FieldData<?> getFieldData(String fieldName) {
//        if (!fieldData.containsKey(fieldName)) {
//            throw new IllegalArgumentException(String.format("Field '%s' is not registered.", fieldName));
//        }

        return fieldData.getOrDefault(fieldName, null);
    }

    public <T> FieldData<T> getFieldData(PropertyInfo<T> property) {
        return (FieldData<T>) getFieldData(property.getName());
    }

    /**
     * Retrieves the field data for the specified property, creating it if it does not already exist. This method is synchronized to ensure thread safety when accessing and modifying the field data map.
     *
     * @param <T> the type of the property
     * @param property the property for which to retrieve or create the field data
     * @return the field data for the specified property
     */
    public synchronized <T> FieldData<T> getOrCreateFieldData(PropertyInfo<T> property) {
        if (!fieldData.containsKey(property.getName())) {
            var field = property.newFieldData(property.getName());
            fieldData.put(property.getName(), field);
        }
        return (FieldData<T>) fieldData.get(property.getName());
    }

    /**
     * Sets the field data for the specified property and value, creating the field data if it does not already exist. This method is synchronized to ensure thread safety when accessing and modifying the field data map.
     *
     * @param <T> the type of the property
     * @param property the property for which to set the field data
     * @param value the value to set for the field data
     */
    public <T> void setFieldData(PropertyInfo<T> property, T value) {
        FieldData<T> field = getOrCreateFieldData(property);
        field.setValue(value);
    }

    /**
     * Loads the field data for the specified property and value, marking it as unchanged.
     *
     * @param property the property for which to load the field data
     * @param value    the value to set for the field data
     * @param <T>      the type of the property
     * @return the loaded field data
     */
    @SuppressWarnings("UnusedReturnValue")
    public <T> FieldData<T> loadFieldData(PropertyInfo<T> property, T value) {
        var field = getOrCreateFieldData(property);
        field.setValue(value);
        field.markAsUnchanged();
        return field;
    }

    /**
     * Removes the field data associated with the specified property, if it exists.
     *
     * @param property the property for which to remove the field data
     */
    public void removeFieldData(PropertyInfo<?> property) {
        fieldData.remove(property.getName());
    }

    /**
     * Checks if field data exists for the specified field name.
     *
     * @param fieldName the name of the field to check
     * @return true if field data exists for the specified field name, false otherwise
     */
    public boolean fieldExists(String fieldName) {
        return fieldData.containsKey(fieldName);
    }

    /**
     * Checks if field data exists for the specified property.
     *
     * @param property the property to check
     * @return true if field data exists for the specified property, false otherwise
     */
    public boolean fieldExists(PropertyInfo<?> property) {
        return fieldExists(property.getName());
    }

    /**
     * Initializes the static fields of the specified business object type to ensure that all static properties are registered.
     *
     * @param businessObjectType the business object type for which to initialize static fields
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public synchronized static void initStaticFields(Class<?> businessObjectType) {
        var fields = businessObjectType.getDeclaredFields();
        for (var field : fields) {
            if (field.getModifiers() == java.lang.reflect.Modifier.STATIC) {
                field.setAccessible(true);
                try {
                    field.get(null);
                } catch (IllegalAccessException e) {
                    //
                }
            }
        }
    }

    public boolean isBusy() {
        return fieldData.values().stream().anyMatch(FieldData::isBusy);
    }
}
