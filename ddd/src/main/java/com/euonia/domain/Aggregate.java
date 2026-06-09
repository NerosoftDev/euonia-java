package com.euonia.domain;

/**
 * The Aggregate interface represents an aggregate root in a domain-driven design (DDD) context.
 * An aggregate is a cluster of domain objects that can be treated as a single unit for data changes.
 * The aggregate root is the main entity that controls access to the other entities within the aggregate and is responsible for enforcing the consistency of the aggregate as a whole.
 *
 * @param <ID> the type of the identifier for the aggregate
 */
public interface Aggregate<ID extends Comparable<ID>> extends Entity<ID> {

}
