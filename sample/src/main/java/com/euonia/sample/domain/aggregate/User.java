package com.euonia.sample.domain.aggregate;

import com.euonia.osba.EditableObject;

public class User extends EditableObject<User> {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
