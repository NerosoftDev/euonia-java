package com.euonia.domain;

public interface Entity<ID extends Comparable<ID>> {
    ID getId();

    void setId(ID id);

    Object[] getKeys();
}
