package com.euonia.domain.event;

import com.euonia.core.ObjectId;
import com.euonia.reflection.TypeHelper;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Base implementation of the Event interface.
 * This class provides common properties and methods for all events, such as event ID, event intent, originator type, and originator ID.
 * It also includes a sequence number to maintain the order of events.
 * Subclasses can extend this base class to create specific types of events with additional properties and behavior.
 */
public abstract class EventBase implements Event {
    private static final String PROPERTY_ID = "nerosoft.euonia.internal.event.id";
    private static final String PROPERTY_EVENT_INTENT = "nerosoft.euonia.internal.event.intent";
    private static final String PROPERTY_ORIGINATOR_TYPE = "nerosoft.euonia.internal.event.originator.type";
    private static final String PROPERTY_ORIGINATOR_ID = "nerosoft.euonia.internal.event.originator.id";
    private final Map<String, String> properties = new HashMap<>();
    private long sequence = Instant.now().toEpochMilli();

    protected EventBase() {
        var type = getClass();
        properties.put(PROPERTY_ID, ObjectId.guid().toString());
        setEventIntent(type.getName());
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

    public String getEventIntent() {
        return get(PROPERTY_EVENT_INTENT);
    }

    public void setEventIntent(String eventIntent) {
        set(PROPERTY_EVENT_INTENT, eventIntent);
    }

    public final String getEventId() {
        return get(PROPERTY_ID);
    }

    public final void setEventId(String eventId) {
        set(PROPERTY_ID, eventId);
    }

    public final long getSequence() {
        return sequence;
    }

    public final void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public String getOriginatorType() {
        return get(PROPERTY_ORIGINATOR_TYPE);
    }

    public void setOriginatorType(String originatorType) {
        set(PROPERTY_ORIGINATOR_TYPE, originatorType);
    }

    public String getOriginatorId() {
        return get(PROPERTY_ORIGINATOR_ID);
    }

    public void setOriginatorId(String originatorId) {
        set(PROPERTY_ORIGINATOR_ID, originatorId);
    }
}
