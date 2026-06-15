package com.euonia.sample.application.command;

import com.euonia.domain.command.CommandBase;

public class UserUpdateCommand extends CommandBase {
    private final long id;

    public UserUpdateCommand(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
