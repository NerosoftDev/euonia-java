package com.euonia.domain.event;

import com.euonia.domain.Aggregate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class EventAggregate implements Aggregate<String> {

    private String id;
    private String eventId;
    private Instant timestamp;
    private String typeName;
    private String eventIntent;
    private String originatorType;
    private String originatorId;
    private Object eventPayload;
    private long eventSequence;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = Instant.parse(timestamp);
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = Instant.ofEpochMilli(timestamp);
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp.toInstant(java.time.ZoneOffset.UTC);
    }

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp.toInstant();
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getEventIntent() {
        return eventIntent;
    }

    public void setEventIntent(String eventIntent) {
        this.eventIntent = eventIntent;
    }

    public String getOriginatorType() {
        return originatorType;
    }

    public void setOriginatorType(String originatorType) {
        this.originatorType = originatorType;
    }

    public String getOriginatorId() {
        return originatorId;
    }

    public void setOriginatorId(String originatorId) {
        this.originatorId = originatorId;
    }

    public Object getEventPayload() {
        return eventPayload;
    }

    public void setEventPayload(Object eventPayload) {
        this.eventPayload = eventPayload;
    }

    public long getEventSequence() {
        return eventSequence;
    }

    public void setEventSequence(long eventSequence) {
        this.eventSequence = eventSequence;
    }

    @Override
    public Object[] getKeys() {
        return new Object[]{id};
    }

    @Override
    public String toString() {
        return eventIntent;
    }
}
