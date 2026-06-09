package com.euonia.domain;

import com.euonia.domain.event.DomainEventBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The AggregateBase class is an abstract base class for implementing the Aggregate interface in a domain-driven design (DDD) context.
 * It provides a common implementation for managing domain events and their handlers within an aggregate root.
 * An aggregate is a cluster of domain objects that can be treated as a single unit for data changes.
 * The aggregate root is the main entity that controls access to the other entities within the aggregate and is responsible for enforcing the consistency of the aggregate as a whole.
 *
 * @param <ID> the type of the identifier for the aggregate, which must be comparable.
 */
public abstract class AggregateBase<ID extends Comparable<ID>> extends EntityBase<ID> implements Aggregate<ID>, HasDomainEvents {
    private final List<DomainEventBase> events = new ArrayList<>();
    private final Map<Class<? extends DomainEventBase>, Consumer<DomainEventBase>> eventHandlers = new HashMap<>();

    public <E extends DomainEventBase> void registerEvent(Class<E> eventType, Consumer<E> handler) {
        eventHandlers.put(eventType, event -> handler.accept(eventType.cast(event)));
    }

    @Override
    public List<DomainEventBase> getEvents() {
        return List.copyOf(events);
    }

    @Override
    public <E extends DomainEventBase> void raiseEvent(E event) {
        applyEvent(event);
        events.add(event);
    }

    @Override
    public <E extends DomainEventBase> void applyEvent(E event) {
        var handler = eventHandlers.getOrDefault(event.getClass(), null);
        if (handler != null) {
            handler.accept(event);
        }
    }

    @Override
    public void clearEvents() {
        events.clear();
    }

    @Override
    public void attachEvents() {
        for (DomainEventBase event : events) {
            event.attach(this);
        }
    }
}
