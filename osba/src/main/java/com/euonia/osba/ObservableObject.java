package com.euonia.osba;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import com.euonia.osba.abstracts.OperableProperty;
import com.euonia.osba.abstracts.TrackableObject;
import com.euonia.reflection.PropertyInfo;

/**
 * Represents an observable business object that can track its edit state (new, changed, deleted) and manage its busy state.
 * This class provides methods to mark the object as new, changed, or deleted, and to check whether the object is busy or savable.
 * It also implements property access methods that respect the object's rules and permissions.
 *
 * @param <T> the type of the observable object
 */
@SuppressWarnings("unused")
public abstract class ObservableObject<T extends ObservableObject<T>> extends BusinessObject<T> implements TrackableObject, OperableProperty {
    private ObjectEditState state;
    private boolean checkObjectRulesOnDelete;
    private final AtomicInteger busyCounter = new AtomicInteger();
    private final Object lock = new Object();
    private final List<Consumer<Boolean>> busyListeners = new CopyOnWriteArrayList<>();

    /**
     * Gets the current edit state of the object, which indicates whether the object is new, changed, or deleted.
     *
     * @return the current edit state of the object
     */
    public final ObjectEditState getState() {
        return state;
    }

    private void setState(ObjectEditState state) {
        this.state = state;
    }

    /**
     * Checks whether the object is new, which means it has been created but not yet saved to the database.
     *
     * @return true if the object is new, false otherwise
     */
    @Override
    public final boolean isNew() {
        return state == ObjectEditState.NEW;
    }

    /**
     * Checks whether the object has been changed, which means it has been modified since it was last saved to the database.
     *
     * @return true if the object has been changed, false otherwise
     */
    @Override
    public final boolean isChanged() {
        return state == ObjectEditState.CHANGED;
    }

    /**
     * Checks whether the object has been marked as deleted, which means it has been flagged for deletion but not yet removed from the database.
     *
     * @return true if the object has been marked as deleted, false otherwise
     */
    @Override
    public final boolean isDeleted() {
        return state == ObjectEditState.DELETED;
    }

    /**
     * Checks whether object rules should be checked when the object is marked as deleted.
     * This flag can be set when marking the object as deleted to indicate whether any business rules should be evaluated before allowing the deletion to proceed.
     *
     * @return true if object rules should be checked on delete, false otherwise
     */
    public final boolean isCheckObjectRulesOnDelete() {
        return checkObjectRulesOnDelete;
    }

    /**
     * Marks the object as new, indicating that it has been created but not yet saved to the database.
     * This method sets the object's edit state to NEW, which can be used to track changes and determine whether the object needs to be saved.
     */
    public final void markAsNew() {
        setState(ObjectEditState.NEW);
    }

    /**
     * Marks the object as changed, indicating that it has been modified since it was last saved to the database.
     * This method sets the object's edit state to CHANGED, which can be used to track changes and determine whether the object needs to be saved.
     */
    public final void markAsChanged() {
        setState(ObjectEditState.CHANGED);
    }

    /**
     * Marks the object as deleted, indicating that it has been flagged for deletion but not yet removed from the database.
     * This method sets the object's edit state to DELETED, which can be used to track changes and determine whether the object needs to be deleted.
     */
    public final void markAsDeleted() {
        markAsDeleted(false);
    }

    /**
     * Marks the object as deleted, indicating that it has been flagged for deletion but not yet removed from the database.
     * This method sets the object's edit state to DELETED, which can be used to track changes and determine whether the object needs to be deleted.
     *
     * @param checkObjectRules whether to check object rules before allowing the deletion to proceed
     */
    public final void markAsDeleted(boolean checkObjectRules) {
        setState(ObjectEditState.DELETED);
        checkObjectRulesOnDelete = checkObjectRules;
    }

    /**
     * Checks whether the object is currently busy, which means it is performing an operation that prevents it from being saved or modified.
     * This method checks both the object's own busy state and the busy state of any related fields or rules to determine whether the object is currently busy.
     *
     * @return true if the object is busy, false otherwise
     */
    @Override
    public boolean isBusy() {
        return isSelfBusy() || (getFieldManager().isBusy());
    }

    /**
     * Checks whether the object itself is busy, which means it is performing an operation that prevents it from being saved or modified.
     * This method checks the object's own busy state and the busy state of any running rules to determine whether the object is currently busy.
     *
     * @return true if the object itself is busy, false otherwise
     */
    public boolean isSelfBusy() {
        return busyCounter.get() > 0 || getRules().hasRunningRules();
    }

    /**
     * Checks whether the object is savable, which means it is valid, has changes, and is not busy.
     * This method can be used to determine whether the object can be saved to the database.
     *
     * @return true if the object is savable, false otherwise
     */
    @Override
    public boolean isSavable() {
        return isValid() && (hasChangedProperties() || isChanged()) && !isBusy();
    }

    /**
     * Marks the object as busy, indicating that it is performing an operation that prevents it from being saved or modified.
     * This method increments the busy counter and notifies any listeners if the object becomes busy.
     */
    protected void markAsBusy() {
        var updateValue = busyCounter.incrementAndGet();

        if (updateValue == 1) {
            notifyBusyChanged(true);
        }
    }

    /**
     * Marks the object as idle, indicating that it is no longer performing an operation that prevents it from being saved or modified.
     * This method decrements the busy counter and notifies any listeners if the object becomes idle.
     */
    protected void markAsIdle() {
        var updateValue = busyCounter.decrementAndGet();

        if (updateValue == 0) {
            notifyBusyChanged(false);
        }
    }

    /**
     * Subscribes a listener to be notified when the busy state of the object changes.
     *
     * @param listener the listener to be notified
     */
    public final void onBusyChanged(Consumer<Boolean> listener) {
        addBusyChangedListener(listener);
    }

    public final void addBusyChangedListener(Consumer<Boolean> listener) {
        if (listener == null) {
            return;
        }
        busyListeners.add(listener);
    }

    public final void removeBusyChangedListener(Consumer<Boolean> listener) {
        if (listener == null) {
            return;
        }
        busyListeners.remove(listener);
    }

    private void notifyBusyChanged(boolean busy) {
        for (Consumer<Boolean> listener : busyListeners) {
            listener.accept(busy);
        }
    }

    // region Get/Set Properties
    protected <V> V getProperty(String propertyName, V field, V defaultValue) {
        var propertyInfo = getFieldManager().getRegisteredProperty(propertyName);
        if (isBypassingRuleCheck() || canReadProperty(propertyInfo, true)) {
            return field;
        }
        return defaultValue;
    }

    protected <V> V getProperty(PropertyInfo<V> propertyInfo, V field) {
        return getProperty(propertyInfo, field, propertyInfo.getDefaultValue());
    }

    protected <V> V getProperty(PropertyInfo<V> propertyInfo, V field, V defaultValue) {
        return getProperty(propertyInfo.getName(), field, defaultValue);
    }

    @Override
    public <V> V getProperty(PropertyInfo<V> propertyInfo) {
        V value;
        if (isBypassingRuleCheck() || canReadProperty(propertyInfo, true)) {
            value = readProperty(propertyInfo);
        } else {
            value = propertyInfo.getDefaultValue();
        }
        return value;
    }

    @Override
    public <V> void setProperty(PropertyInfo<V> propertyInfo, V newValue) {
        if (!isBypassingRuleCheck() && !canWriteProperty(propertyInfo, true)) {
            return;
        }

        V oldValue;
        var fieldData = getFieldManager().getFieldData(propertyInfo);
        if (fieldData == null) {
            oldValue = propertyInfo.getDefaultValue();
            getFieldManager().loadFieldData(propertyInfo, newValue);
        } else {
            oldValue = fieldData.getValue();
        }

        loadPropertyValue(propertyInfo, oldValue, newValue, !isBypassingRuleCheck());
    }
    // endregion

}
