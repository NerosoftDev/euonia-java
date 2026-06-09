package com.euonia.osba;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import com.euonia.osba.abstracts.TrackableObject;

/**
 * Observable list with child property notifications, collection change notifications,
 * and aggregate busy-state notifications.
 *
 * @param <TItem> item type
 */
@SuppressWarnings("unused")
public class ObservableList<TItem> extends ArrayList<TItem> {
    private final List<Consumer<ObjectChangedEventArgs>> childChangedListeners = new CopyOnWriteArrayList<>();
    private final List<Consumer<ObjectChangedEventArgs>> itemPropertyChangedListeners = new CopyOnWriteArrayList<>();
    private final List<Consumer<ObjectChangedEventArgs>> collectionChangedListeners = new CopyOnWriteArrayList<>();
    private final List<Consumer<Boolean>> busyChangedListeners = new CopyOnWriteArrayList<>();
    private final Map<TItem, PropertyChangeListener> propertyChangeListeners = new IdentityHashMap<>();
    private final Map<TItem, Consumer<Boolean>> busyListeners = new IdentityHashMap<>();

    private boolean raiseListChangedEvents = true;

    public ObservableList() {
        super();
    }

    public ObservableList(Collection<? extends TItem> items) {
        super(items);
        for (TItem item : items) {
            hookItem(item);
        }
    }

    public boolean isRaiseListChangedEvents() {
        return raiseListChangedEvents;
    }

    public void setRaiseListChangedEvents(boolean raiseListChangedEvents) {
        this.raiseListChangedEvents = raiseListChangedEvents;
    }

    public void addChildChangedListener(Consumer<ObjectChangedEventArgs> listener) {
        if (listener == null) {
            return;
        }
        childChangedListeners.add(listener);
    }

    public void removeChildChangedListener(Consumer<ObjectChangedEventArgs> listener) {
        if (listener == null) {
            return;
        }
        childChangedListeners.remove(listener);
    }

    /**
     * Subscribes to property-change events from items in this list.
     * The event args will have a non-null {@code propertyChangedEvent} and
     * a null {@code collectionChangeType}.
     */
    public void addItemPropertyChangedListener(Consumer<ObjectChangedEventArgs> listener) {
        if (listener == null) {
            return;
        }
        itemPropertyChangedListeners.add(listener);
    }

    public void removeItemPropertyChangedListener(Consumer<ObjectChangedEventArgs> listener) {
        if (listener == null) {
            return;
        }
        itemPropertyChangedListeners.remove(listener);
    }

    /**
     * Subscribes to collection-structure change events (add, remove, replace, clear).
     * The event args will have a non-null {@code collectionChangeType} and
     * a null {@code propertyChangedEvent}.
     */
    public void addCollectionChangedListener(Consumer<ObjectChangedEventArgs> listener) {
        if (listener == null) {
            return;
        }
        collectionChangedListeners.add(listener);
    }

    public void removeCollectionChangedListener(Consumer<ObjectChangedEventArgs> listener) {
        if (listener == null) {
            return;
        }
        collectionChangedListeners.remove(listener);
    }

    public void addBusyChangedListener(Consumer<Boolean> listener) {
        if (listener == null) {
            return;
        }
        busyChangedListeners.add(listener);
    }

    public void removeBusyChangedListener(Consumer<Boolean> listener) {
        if (listener == null) {
            return;
        }
        busyChangedListeners.remove(listener);
    }

    public boolean isBusy() {
        for (TItem item : this) {
            if (item instanceof TrackableObject trackableObject && trackableObject.isBusy()) {
                return true;
            }
        }
        return false;
    }

    public AutoCloseable suppressListChangedEvents() {
        return new SuppressListChangedEventsScope();
    }

    @Override
    public boolean add(TItem item) {
        var result = super.add(item);
        if (result) {
            hookItem(item);
            raiseCollectionChanged(item, ObjectChangedEventArgs.CollectionChangeType.ADD, size() - 1);
            raiseBusyChanged();
        }
        return result;
    }

    @Override
    public void add(int index, TItem item) {
        super.add(index, item);
        hookItem(item);
        raiseCollectionChanged(item, ObjectChangedEventArgs.CollectionChangeType.ADD, index);
        raiseBusyChanged();
    }

    @Override
    public boolean addAll(Collection<? extends TItem> items) {
        if (items == null || items.isEmpty()) {
            return false;
        }

        var index = size();
        var result = super.addAll(items);
        if (result) {
            for (TItem item : items) {
                hookItem(item);
                raiseCollectionChanged(item, ObjectChangedEventArgs.CollectionChangeType.ADD, index++);
            }
            raiseBusyChanged();
        }
        return result;
    }

    @Override
    public boolean addAll(int index, Collection<? extends TItem> items) {
        if (items == null || items.isEmpty()) {
            return false;
        }

        var result = super.addAll(index, items);
        if (result) {
            var currentIndex = index;
            for (TItem item : items) {
                hookItem(item);
                raiseCollectionChanged(item, ObjectChangedEventArgs.CollectionChangeType.ADD, currentIndex++);
            }
            raiseBusyChanged();
        }
        return result;
    }

    @Override
    public TItem remove(int index) {
        TItem removed = super.remove(index);
        unhookItem(removed);
        raiseCollectionChanged(removed, ObjectChangedEventArgs.CollectionChangeType.REMOVE, index);
        raiseBusyChanged();
        return removed;
    }

    @Override
    public boolean remove(Object item) {
        int index = indexOf(item);
        if (index < 0) {
            return false;
        }

        TItem removed = super.remove(index);
        unhookItem(removed);
        raiseCollectionChanged(removed, ObjectChangedEventArgs.CollectionChangeType.REMOVE, index);
        raiseBusyChanged();
        return true;
    }

    @Override
    public TItem set(int index, TItem item) {
        TItem oldItem = super.set(index, item);
        unhookItem(oldItem);
        hookItem(item);
        raiseCollectionChanged(item, ObjectChangedEventArgs.CollectionChangeType.REPLACE, index);
        raiseBusyChanged();
        return oldItem;
    }

    @Override
    public void clear() {
        if (isEmpty()) {
            return;
        }

        for (TItem item : this) {
            unhookItem(item);
        }
        super.clear();
        raiseCollectionChanged(null, ObjectChangedEventArgs.CollectionChangeType.CLEAR, -1);
        raiseBusyChanged();
    }

    private void hookItem(TItem item) {
        if (item == null) {
            return;
        }

        if (item instanceof BusinessObject<?> businessObject) {
            PropertyChangeListener listener = event -> onItemPropertyChanged(item, event);
            propertyChangeListeners.put(item, listener);
            businessObject.addPropertyChangeListener(listener);
        }

        if (item instanceof ObservableObject<?> observableObject) {
            Consumer<Boolean> listener = busy -> onItemBusyChanged();
            busyListeners.put(item, listener);
            observableObject.addBusyChangedListener(listener);
        }
    }

    private void unhookItem(TItem item) {
        if (item == null) {
            return;
        }

        if (item instanceof BusinessObject<?> businessObject) {
            PropertyChangeListener listener = propertyChangeListeners.remove(item);
            if (listener != null) {
                businessObject.removePropertyChangeListener(listener);
            }
        }

        if (item instanceof ObservableObject<?> observableObject) {
            Consumer<Boolean> listener = busyListeners.remove(item);
            if (listener != null) {
                observableObject.removeBusyChangedListener(listener);
            }
        }
    }

    protected void onChildChanged(ObjectChangedEventArgs args) {
        for (Consumer<ObjectChangedEventArgs> listener : childChangedListeners) {
            listener.accept(args);
        }
    }

    protected void onBusyChanged(boolean busy) {
        for (Consumer<Boolean> listener : busyChangedListeners) {
            listener.accept(busy);
        }
    }

    private void onItemPropertyChanged(TItem item, PropertyChangeEvent event) {
        var args = new ObjectChangedEventArgs(item, event, null, -1);
        onChildChanged(args);
        for (Consumer<ObjectChangedEventArgs> listener : itemPropertyChangedListeners) {
            listener.accept(args);
        }
    }

    private void onItemBusyChanged() {
        raiseBusyChanged();
    }

    private void raiseCollectionChanged(TItem item, ObjectChangedEventArgs.CollectionChangeType changeType, int index) {
        if (!raiseListChangedEvents) {
            return;
        }
        var args = new ObjectChangedEventArgs(item, null, changeType, index);
        onChildChanged(args);
        for (Consumer<ObjectChangedEventArgs> listener : collectionChangedListeners) {
            listener.accept(args);
        }
    }

    private void raiseBusyChanged() {
        onBusyChanged(isBusy());
    }

    private final class SuppressListChangedEventsScope implements AutoCloseable {
        private final boolean previousValue;

        private SuppressListChangedEventsScope() {
            this.previousValue = raiseListChangedEvents;
            raiseListChangedEvents = false;
        }

        @Override
        public void close() {
            raiseListChangedEvents = previousValue;
        }
    }
}
