package com.euonia.osba.rules;

import com.euonia.reflection.PropertyInfo;

import java.lang.reflect.Member;
import java.lang.reflect.Type;

/**
 * Provides a base implementation for rules, including common properties and methods.
 * This class can be extended to create specific rule implementations.
 */
public abstract class RuleBase implements Rule {
    private static final String NAME_PREFIX = "rule://";

    private final String name;

    private PropertyInfo<?> property;

    protected RuleBase() {
        name = generateName(getClass());
    }

    protected RuleBase(PropertyInfo<?> property) {
        name = generateName(getClass(), property.getName());
        this.property = property;
    }

    protected RuleBase(PropertyInfo<?> property, Member member) {
        name = generateName(getClass(), property.getName(), member.getName());
        this.property = property;
    }

    public final PropertyInfo<?> getProperty() {
        return property;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    private static String generateName(Type type, String... names) {
        var typeName = type.getTypeName();
        return generateName(typeName, names);
    }

    private static String generateName(String typeName, String... names) {
        var builder = new StringBuilder(NAME_PREFIX + typeName);
        for (var name : names) {
            builder.append("/").append(name);
        }
        return builder.toString();
    }
}
