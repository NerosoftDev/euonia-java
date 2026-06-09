package com.euonia.sample.domain.event;

import com.euonia.domain.event.DomainEventBase;

public class UserCreatedEvent extends DomainEventBase {
    private final long id;
    private final String name;

    public UserCreatedEvent(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
