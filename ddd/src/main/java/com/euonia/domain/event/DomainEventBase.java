package com.euonia.domain.event;

import com.euonia.core.ObjectId;
import com.euonia.domain.Aggregate;

import java.time.Instant;

/**
 * Base implementation of a domain event, which can be extended by specific domain events.
 * It provides common properties and methods for handling domain events, such as attaching to an aggregate and converting to an event aggregate.
 */
public abstract class DomainEventBase extends EventBase implements DomainEvent {

    private Object aggregatePayload;

    @Override
    public <ID extends Comparable<ID>> void attach(Aggregate<ID> aggregate) {
        setOriginatorId(aggregate.getId().toString());
        setOriginatorType(aggregate.getClass().getTypeName());
        setAggregatePayload(aggregate);
    }

    @Override
    public EventAggregate getEventAggregate() {
        return new EventAggregate() {{
            setId(ObjectId.guid().toString());
            setTypeName(getClass().getTypeName());
            setEventIntent(getEventIntent());
            setEventId(getEventId());
            setTimestamp(Instant.now());
            setOriginatorId(getOriginatorId());
            setOriginatorType(getOriginatorType());
            setEventSequence(getSequence());
            setAggregatePayload(this);
        }};
    }

    public Object getAggregatePayload() {
        return aggregatePayload;
    }

    public void setAggregatePayload(Object aggregatePayload) {
        this.aggregatePayload = aggregatePayload;
    }

    @SuppressWarnings("unchecked")
    public <A extends Aggregate<?>> A getAggregate(Class<A> type) {
        if (aggregatePayload == null) {
            return null;
        }

        if (aggregatePayload.getClass().isAssignableFrom(type)) {
            return (A) aggregatePayload;
        }
        return null;
    }
}
