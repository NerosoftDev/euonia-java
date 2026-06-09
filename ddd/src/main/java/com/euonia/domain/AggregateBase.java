package com.euonia.domain;

import com.euonia.domain.event.DomainEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
    private final List<DomainEvent> events = new ArrayList<>();
    private final ConcurrentMap<Class<? extends DomainEvent>, List<Consumer<DomainEvent>>> eventHandlers = new ConcurrentHashMap<>();

    public <E extends DomainEvent> void registerEvent(Class<E> eventType, Consumer<E> handler) {
        eventHandlers.computeIfAbsent(eventType, k -> new ArrayList<>())
                     .add(event -> handler.accept(eventType.cast(event)));
    }

    @Override
    public List<DomainEvent> getEvents() {
        return List.copyOf(events);
    }

    @Override
    public <E extends DomainEvent> void raiseEvent(E event) {
        applyEvent(event);
        events.add(event);
    }

    @Override
    public <E extends DomainEvent> void applyEvent(E event) {
        var handlers = eventHandlers.getOrDefault(event.getClass(), null);
        if (handlers != null) {
            handlers.forEach(handler -> handler.accept(event));
        }
    }

    @Override
    public void clearEvents() {
        events.clear();
    }

    @Override
    public void attachEvents() {
        for (DomainEvent event : events) {
            event.attach(this);
        }
    }
}
