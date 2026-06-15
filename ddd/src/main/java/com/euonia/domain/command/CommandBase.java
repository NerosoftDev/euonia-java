package com.euonia.domain.command;

import com.euonia.core.GuidType;
import com.euonia.core.ObjectId;
import com.euonia.reflection.TypeHelper;

import java.util.HashMap;
import java.util.Map;

public abstract class CommandBase implements Command {
    private static final String PROPERTY_ID = "nerosoft.euonia.internal.command.id";

    private final Map<String, String> properties = new HashMap<>();

    protected CommandBase() {
        properties.put(PROPERTY_ID, ObjectId.newGuid(GuidType.SEQUENTIAL_AS_STRING).toString());
    }

    @Override
    public String getCommandId() {
        return properties.get(PROPERTY_ID);
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public final String get(String property) {
        return properties.getOrDefault(property, null);
    }

    public final void set(String property, String value) {
        properties.put(property, value);
    }

    public <T> T get(String property, Class<T> castType) {
        var value = properties.getOrDefault(property, null);
        if (value == null) {
            return null;
        }
        return TypeHelper.coerceValue(castType, value);
    }
}
