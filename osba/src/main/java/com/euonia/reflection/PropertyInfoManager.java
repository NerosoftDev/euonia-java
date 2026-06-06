package com.euonia.reflection;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class PropertyInfoManager {
    private static Map<Type, PropertyInfoList> propertyCache;

    private synchronized static Map<Type, PropertyInfoList> getPropertyCache() {
        if (propertyCache == null) {
            propertyCache = new HashMap<>();
        }
        return propertyCache;
    }

    public synchronized static PropertyInfoList getPropertyListCache(Type type) {
        var cache = getPropertyCache();
        if (!cache.containsKey(type)) {
            PropertyInfoList propertyInfoList = new PropertyInfoList();
            cache.put(type, propertyInfoList);
            FieldDataManager.initStaticFields((Class<?>) type);
        }
        return cache.get(type);
    }

    public static PropertyInfoList getRegisteredProperties(Type type) {
        var list = getPropertyListCache(type);
        return new PropertyInfoList(list);
    }

    @SuppressWarnings("rawtypes")
    public static PropertyInfo getRegisteredProperty(Type type, String propertyName) {
        var properties = getRegisteredProperties(type);
        return properties.stream()
                         .filter(property -> property.getName().equals(propertyName))
                         .findFirst()
                         .orElse(null);
    }

    public synchronized static PropertyInfo<?> registerProperty(Type type, PropertyInfo<?> propertyInfo) {
        var list = getPropertyListCache(type);
        if (list.isLocked()) {
            throw new IllegalStateException("Cannot register property after the list has been locked.");
        }
        var index = list.indexOf(propertyInfo);
        if (index >= 0) {
            throw new IllegalStateException("Property with name '" + propertyInfo.getName() + "' is already registered for type " + type.getTypeName());
        }
        list.add(propertyInfo);
        return propertyInfo;
    }
}
