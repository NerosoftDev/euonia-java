package com.euonia.reflection;

import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PropertyInfoManager {
    private static final ConcurrentMap<Type, PropertyInfoList> propertyCache = new ConcurrentHashMap<>();

    public static PropertyInfoList getPropertyListCache(Type type) {
        return propertyCache.computeIfAbsent(type, t -> {
            PropertyInfoList propertyInfoList = new PropertyInfoList();
            FieldDataManager.initStaticFields((Class<?>) t);
            return propertyInfoList;
        });
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

    public static PropertyInfo<?> registerProperty(final Type type, PropertyInfo<?> propertyInfo) {
        var list = getPropertyListCache(type);
        return list.getOrAdd(propertyInfo);
    }
}
