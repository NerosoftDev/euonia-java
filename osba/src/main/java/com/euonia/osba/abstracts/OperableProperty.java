package com.euonia.osba.abstracts;

import com.euonia.reflection.PropertyInfo;

/**
 * Represents a contract for an object that can get and set properties.
 */
public interface OperableProperty {

    /**
     * Gets the value of a property based on the provided PropertyInfo.
     *
     * @param propertyInfo The PropertyInfo object representing the property to retrieve.
     * @return The value of the property.
     */
    Object getProperty(PropertyInfo propertyInfo);

    /**
     * Sets the value of a property based on the provided PropertyInfo and value.
     *
     * @param propertyInfo The PropertyInfo object representing the property to set.
     * @param value        The value to set for the property.
     */
    void setProperty(PropertyInfo propertyInfo, Object value);
}
