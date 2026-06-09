package com.euonia.osba;

import java.beans.PropertyChangeEvent;

public class ObjectChangedEventArgs {
    private final Object changedObject;
    private final PropertyChangeEvent propertyChangedEvent;
    private final CollectionChangeType collectionChangeType;
    private final int collectionIndex;

    public ObjectChangedEventArgs(Object changedObject) {
        this(changedObject, null, null, -1);
    }

    public ObjectChangedEventArgs(Object changedObject,
                                  PropertyChangeEvent propertyChangedEvent,
                                  CollectionChangeType collectionChangeType,
                                  int collectionIndex) {
        this.changedObject = changedObject;
        this.propertyChangedEvent = propertyChangedEvent;
        this.collectionChangeType = collectionChangeType;
        this.collectionIndex = collectionIndex;
    }

    public Object getChangedObject() {
        return changedObject;
    }

    public PropertyChangeEvent getPropertyChangedEvent() {
        return propertyChangedEvent;
    }

    public CollectionChangeType getCollectionChangeType() {
        return collectionChangeType;
    }

    public int getCollectionIndex() {
        return collectionIndex;
    }

    public enum CollectionChangeType {
        ADD,
        REMOVE,
        REPLACE,
        CLEAR
    }
}
