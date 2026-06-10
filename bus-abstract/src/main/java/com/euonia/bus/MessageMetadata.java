package com.euonia.bus;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MessageMetadata implements Map<String, Object> {
    private final Map<String, Object> metadata = new HashMap<>();

    @Override
    public int size() {
        return metadata.size();
    }

    @Override
    public boolean isEmpty() {
        return metadata.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return metadata.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return metadata.containsValue(value);
    }

    public Object get(String key) {
        return metadata.getOrDefault(key, null);
    }

    public <T> T get(String key, Class<T> type) {
        Object value = metadata.get(key);
        if (value == null) {
            return null;
        }
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        throw new IllegalStateException(
            String.format("Value for key '%s' is not of type %s", key, type.getName()));
    }

    @Override
    public Object get(Object key) {
        return metadata.getOrDefault(key, null);
    }

    @Override
    public Object put(String key, Object value) {
        return metadata.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return metadata.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        metadata.putAll(m);
    }

    @Override
    public void clear() {
        metadata.clear();
    }

    @Override
    public Set<String> keySet() {
        return metadata.keySet();
    }

    @Override
    public Collection<Object> values() {
        return metadata.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return metadata.entrySet();
    }

}
