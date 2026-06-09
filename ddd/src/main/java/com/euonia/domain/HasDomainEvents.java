package com.euonia.domain;

import com.euonia.domain.event.DomainEvent;

import java.util.List;
import java.util.function.Consumer;

/**
 * The HasDomainEvents interface defines the contract for an aggregate that can raise and manage domain events in a domain-driven design (DDD) context.
 * An aggregate is a cluster of domain objects that can be treated as a single unit for data changes.
 * The aggregate root is the main entity that controls access to the other entities within the aggregate and is responsible for enforcing the consistency of the aggregate as a whole.
 * Domain events represent significant occurrences or changes in the state of the aggregate that are relevant to the business logic and can be used for event sourcing, auditing, or integration with other systems.
 */
public interface HasDomainEvents {
    /**
     * Gets the list of domain events that have been raised by this aggregate.
     * Domain events represent significant occurrences or changes in the state of the aggregate that are relevant to the business logic and can be used for event sourcing, auditing, or integration with other systems.
     *
     * @return the list of domain events raised by this aggregate
     */
    List<DomainEvent> getEvents();

    /**
     * Registers a handler for a specific type of domain event. The handler will be invoked when an event of the specified type is raised.
     *
     * @param eventType the type of domain event to register the handler for
     * @param handler   the handler to be invoked when the event is raised
     * @param <E>       the type of the domain event
     */
    <E extends DomainEvent> void registerEvent(Class<E> eventType, Consumer<E> handler);

    /**
     * Raises a domain event. The event will be processed by the registered handlers.
     *
     * @param event the domain event to raise
     * @param <E>   the type of the domain event
     */
    <E extends DomainEvent> void raiseEvent(E event);

    /**
     * Applies a domain event to the aggregate. This method is typically used to update the state of the aggregate based on the event.
     *
     * @param event the domain event to apply
     * @param <E>   the type of the domain event
     */
    <E extends DomainEvent> void applyEvent(E event);

    /**
     * Clears all domain events that have been raised by this aggregate.
     */
    void clearEvents();

    /**
     * Attaches all domain events to the aggregate. This method is typically used to initialize the aggregate with existing events.
     */
    void attachEvents();
}
