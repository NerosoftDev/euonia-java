package com.euonia.osba;

import com.euonia.osba.abstracts.RuleCheckable;
import com.euonia.osba.abstracts.UseBusinessContext;
import com.euonia.osba.rules.BrokenRuleCollection;
import com.euonia.osba.rules.RuleManager;
import com.euonia.osba.rules.Rules;
import com.euonia.reflection.FieldDataManager;
import com.euonia.reflection.PropertyInfo;
import com.euonia.security.UnauthorizedAccessException;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class BusinessObject<B extends BusinessObject<B>> implements UseBusinessContext, RuleCheckable {
    private final List<PropertyInfo<?>> changedProperties = new ArrayList<>();
    private volatile FieldDataManager fieldManager;
    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private BusinessContext businessContext;

    @Override
    public void setBusinessContext(BusinessContext businessContext) {
        this.businessContext = businessContext;
        onBusinessContextSet();
        initialize();
        initializeRules();
    }

    @Override
    public BusinessContext getBusinessContext() {
        return businessContext;
    }

    protected void onBusinessContextSet() {

    }

    protected void initialize() {

    }

    private void initializeRules() {
        var rules = RuleManager.getRules(this.getClass());
        if (rules.isInitialized()) {
            return;
        }
        synchronized (rules) {
            if (rules.isInitialized()) {
                return;
            }

            try {
                addRules();
                rules.setInitialized(true);
            } catch (Exception e) {
                RuleManager.cleanRules(this.getClass());
                throw e;
            }
        }
    }

    public FieldDataManager getFieldManager() {
        if (fieldManager == null) {
            synchronized (this) {
                if (fieldManager == null) {
                    fieldManager = new FieldDataManager(this.getClass());
                }
            }
        }
        return fieldManager;
    }

    private volatile Rules rules;

    protected Rules getRules() {
        if (rules == null) {
            synchronized (this) {
                if (rules == null) {
                    rules = new Rules(this);
                }
            }
        }
        return rules;
    }

    protected void addRules() {

    }

    protected void checkPropertyRules(PropertyInfo<?> propertyInfo, Object oldValue, Object newValue) {
        var properties = getRules().checkObjectRulesAsync()
                                   .thenApply(brokenProperties ->
                                       brokenProperties.stream()
                                                       .filter(p -> p.equals(propertyInfo.getName()))
                                                       .toList())
                                   .join();
        onPropertyChanged(propertyInfo, oldValue, newValue);
    }

    // region RuleCheckable
    @Override
    public boolean isValid() {
        return getRules().isValid();
    }

    @Override
    public void ruleCheckComplete(PropertyInfo<?> property) {
        onPropertyChanged(property, null, null);
    }

    @Override
    public void ruleCheckComplete(String property) {
        onPropertyChanged(property, null, null);
    }

    @Override
    public void allRulesComplete() {

    }

    @Override
    public void suspendRuleChecking() {
        getRules().setSuppressRuleChecking(true);
    }

    @Override
    public void resumeRuleChecking() {
        getRules().setSuppressRuleChecking(false);
    }

    @Override
    public BrokenRuleCollection getBrokenRules() {
        return rules.getBrokenRules();
    }

    // endregion

    // region Property change
    protected List<String> getPropertiesToCheckRulesOnChanged() {
        return List.of();
    }

    public List<PropertyInfo<?>> getChangedProperties() {
        return List.copyOf(changedProperties);
    }

    public boolean hasChangedProperties() {
        return !changedProperties.isEmpty();
    }

    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    protected void onPropertyChanged(String propertyName, Object oldValue, Object newValue) {
        support.firePropertyChange(propertyName, oldValue, newValue);
    }

    protected void onPropertyChanged(PropertyInfo<?> propertyInfo, Object oldValue, Object newValue) {
        onPropertyChanged(propertyInfo.getName(), oldValue, newValue);
    }

    protected final void propertyHasChanged(PropertyInfo<?> propertyInfo, Object oldValue, Object newValue) {
        if (!changedProperties.contains(propertyInfo)) {
            changedProperties.add(propertyInfo);
        }
        if (getPropertiesToCheckRulesOnChanged().contains(propertyInfo.getName())) {
            checkPropertyRules(propertyInfo, oldValue, newValue);
        } else {
            onPropertyChanged(propertyInfo, oldValue, newValue);
        }
    }
    // endregion

    // region Load Properties
    public boolean fieldExists(PropertyInfo<?> propertyInfo) {
        return getFieldManager().fieldExists(propertyInfo);
    }

    public boolean fieldExists(String fieldName) {
        var propertyInfo = getFieldManager().getRegisteredProperty(fieldName);
        return fieldExists(propertyInfo);
    }

    public <T> T readProperty(PropertyInfo<T> propertyInfo) {
        T result;
        var field = getFieldManager().getFieldData(propertyInfo);
        if (field != null) {
            result = field.getValue();
        } else {
            result = propertyInfo.getDefaultValue();
            getFieldManager().loadFieldData(propertyInfo, result);
        }
        return result;
    }

    public Object readProperty(String propertyName) {
        var propertyInfo = getFieldManager().getRegisteredProperty(propertyName);
        return readProperty(propertyInfo);
    }

    public final <T> void loadProperty(PropertyInfo<T> propertyInfo, T value) {
        T oldValue;
        var fieldData = getFieldManager().getFieldData(propertyInfo);
        if (fieldData == null) {
            oldValue = propertyInfo.getDefaultValue();
            getFieldManager().loadFieldData(propertyInfo, oldValue);
        } else {
            oldValue = fieldData.getValue();
        }
        loadPropertyValue(propertyInfo, oldValue, value, false);
    }

    protected final <T> void loadPropertyValue(PropertyInfo<T> propertyInfo, T oldValue, T newValue, boolean markAsChanged) {
        var isValueChanged = !Objects.equals(oldValue, newValue);
        if (!isValueChanged) {
            return;
        }
        if (markAsChanged) {
            getFieldManager().setFieldData(propertyInfo, newValue);
            propertyHasChanged(propertyInfo, oldValue, newValue);
        } else {
            getFieldManager().loadFieldData(propertyInfo, newValue);
        }
    }

    protected final <T> boolean valuesDiffer(PropertyInfo<T> propertyInfo, T oldValue, T newValue) {
        boolean isDifferent;
        if (oldValue == null) {
            isDifferent = newValue != null;
        } else {
            if (propertyInfo.getType().isAssignableFrom(BusinessObject.class)) {
                isDifferent = !Objects.equals(oldValue, newValue);
            } else {
                isDifferent = !oldValue.equals(newValue);
            }
        }
        return isDifferent;
    }
    // endregion

    // region Authorization
    public boolean canReadProperty(PropertyInfo<?> propertyInfo) {
        return true;
    }

    public boolean canReadProperty(String propertyName) {
        var propertyInfo = getFieldManager().getRegisteredProperty(propertyName);
        return canReadProperty(propertyInfo);
    }

    public final boolean canReadProperty(PropertyInfo<?> propertyInfo, boolean throwOnFalse) {
        var result = canReadProperty(propertyInfo);
        if (!result && throwOnFalse) {
            throw new UnauthorizedAccessException(String.format("Property '%s' is not readable.", propertyInfo.getName()));
        }
        return result;
    }

    public final boolean canReadProperty(String propertyName, boolean throwOnFalse) {
        var propertyInfo = getFieldManager().getRegisteredProperty(propertyName);
        return canReadProperty(propertyInfo, throwOnFalse);
    }

    public boolean canWriteProperty(PropertyInfo<?> propertyInfo) {
        return true;
    }

    public final boolean canWriteProperty(PropertyInfo<?> propertyInfo, boolean throwOnFalse) {
        var result = canWriteProperty(propertyInfo);
        if (!result && throwOnFalse) {
            throw new UnauthorizedAccessException(String.format("Property '%s' is not writable.", propertyInfo.getName()));
        }
        return result;
    }

    public boolean canWriteProperty(String propertyName) {
        var propertyInfo = getFieldManager().getRegisteredProperty(propertyName);
        return canWriteProperty(propertyInfo);
    }

    public final boolean canWriteProperty(String propertyName, boolean throwOnFalse) {
        var propertyInfo = getFieldManager().getRegisteredProperty(propertyName);
        return canWriteProperty(propertyInfo, throwOnFalse);
    }
    // endregion

    // region Property Checks
    private boolean isBypassingRuleCheck = false;

    protected boolean isBypassingRuleCheck() {
        return isBypassingRuleCheck;
    }

    private BypassRuleCheckObject<B> internalBypassRuleChecks;

    protected final BypassRuleCheckObject<?> bypassRuleChecks() {
        return BypassRuleCheckObject.create(this);
    }

    protected static final class BypassRuleCheckObject<B extends BusinessObject<B>> implements AutoCloseable {

        private BusinessObject<B> businessObject;

        protected BypassRuleCheckObject(BusinessObject<B> businessObject) {
            this.businessObject = businessObject;
            this.businessObject.isBypassingRuleCheck = true;
        }

        public synchronized static <B extends BusinessObject<B>> BypassRuleCheckObject<B> create(BusinessObject<B> businessObject) {
            businessObject.internalBypassRuleChecks = new BypassRuleCheckObject<>(businessObject);
            businessObject.internalBypassRuleChecks.incrementRefCount();
            return businessObject.internalBypassRuleChecks;
        }

        @Override
        public void close() {
            decrementRefCount();
        }

        private int refCount = 0;

        private synchronized void incrementRefCount() {
            refCount++;
        }

        private synchronized void decrementRefCount() {
            refCount--;
            if (refCount != 0) {
                return;
            }

            this.businessObject.isBypassingRuleCheck = false;
            this.businessObject.internalBypassRuleChecks = null;
            this.businessObject = null;
        }

    }
    // endregion
}
