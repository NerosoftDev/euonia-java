package com.euonia.sample.domain;

import com.euonia.domain.Aggregate;
import com.euonia.domain.HasDomainEvents;
import com.euonia.domain.event.DomainEvent;
import com.euonia.osba.EditableObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

@Component
@Scope("prototype")
public abstract class EditableObjectBase<T extends EditableObjectBase<T, ID>, ID extends Comparable<ID>> extends EditableObject<T> implements Aggregate<ID>, HasDomainEvents {
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
