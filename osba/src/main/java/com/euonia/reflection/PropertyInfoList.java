package com.euonia.reflection;

import java.util.ArrayList;
import java.util.List;

public class PropertyInfoList extends ArrayList<PropertyInfo<?>> {
    public PropertyInfoList() {
        super();
    }

    public PropertyInfoList(List<PropertyInfo<?>> list) {
        super(list);
    }

    private boolean locked = false;

    public boolean isLocked() {
        return locked;
    }

    public void lock() {
        locked = true;
    }

    public void unlock() {
        locked = false;
    }
}
