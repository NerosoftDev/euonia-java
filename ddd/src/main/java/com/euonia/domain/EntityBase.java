package com.euonia.domain;

public abstract class EntityBase<ID extends Comparable<ID>> implements Entity<ID> {
    private ID id;

    @Override
    public ID getId() {
        return id;
    }

    @Override
    public void setId(ID id) {
        this.id = id;
    }

    @Override
    public Object[] getKeys() {
        return new Object[]{id};
    }

    @Override
    public String toString() {
        return String.format("%s{id=%s}", this.getClass().getSimpleName(), id);
    }
}
