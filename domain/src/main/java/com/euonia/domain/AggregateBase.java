package com.euonia.domain;

import com.euonia.domain.event.DomainEventBase;

import java.util.ArrayList;
import java.util.List;

public abstract class AggregateBase<ID extends Comparable<ID>> extends EntityBase<ID> implements Aggregate<ID> {
    private final List<DomainEventBase> events = new ArrayList<>();

    public List<DomainEventBase> getEvents() {
        return List.copyOf(events);
    }

    protected <E extends DomainEventBase> void raiseEvent(E event) {
        events.add(event);
    }

    protected void clearEvents() {
        events.clear();
    }

    public void attachEvents() {
        for (DomainEventBase event : events) {
            event.attach(this);
        }
    }
}
