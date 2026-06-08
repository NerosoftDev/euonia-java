package com.euonia.domain.event;

import com.euonia.domain.Aggregate;

/**
 * The DomainEvent interface represents a specific type of event that is associated with a particular aggregate in the domain model. It extends the Event interface, indicating that it inherits the properties and methods defined in the Event interface while adding additional functionality specific to domain events.
 * Domain events are used to capture and represent significant occurrences or changes that happen within the context of a specific aggregate. They allow for communication and coordination between different components of the system, enabling the propagation of important information about changes in the state of the aggregate to other parts of the system that may be interested in those changes.
 * By attaching a domain event to an aggregate, you can ensure that the event is associated with the specific aggregate instance and can be processed accordingly. The getEventAggregate method allows you to retrieve the associated EventAggregate, which contains relevant information about the event and its context within the domain model.
 */
public interface DomainEvent extends Event {

    /**
     * Attaches the domain event to a specific aggregate. This method allows you to associate the event with a particular aggregate instance, enabling the event to be processed in the context of that aggregate. The generic type parameter <ID> represents the type of the identifier used by the aggregate, which must be comparable.
     *
     * @param aggregate the aggregate to which the domain event should be attached
     * @param <ID>      the type of the identifier used by the aggregate
     */
    <ID extends Comparable<ID>> void attach(Aggregate<ID> aggregate);

    /**
     * Retrieves the EventAggregate associated with this domain event. The EventAggregate contains relevant information about the event and its context within the domain model, such as the event's unique identifier, timestamp, type name, event intent, originator type, originator ID, event payload, and event sequence. This method allows you to access the details of the event and its associated aggregate for further processing or analysis.
     *
     * @return the EventAggregate associated with this domain event
     */
    EventAggregate getEventAggregate();
}
