package com.euonia.sample.application.handler;

import com.euonia.bus.Handler;
import com.euonia.bus.MessageContext;
import com.euonia.factory.ObjectFactory;
import com.euonia.sample.application.command.UserUpdateCommand;
import com.euonia.sample.domain.aggregate.User;

public class UserUpdateCommandHandler implements Handler<UserUpdateCommand, Void> {

    private final ObjectFactory factory;

    public UserUpdateCommandHandler(ObjectFactory factory) {
        this.factory = factory;
    }

    @Override
    public Void handle(UserUpdateCommand message, MessageContext context) {
        var user = factory.fetch(User.class, message.getId());
        try (user) {
            user.onSaved((args) -> {
                System.out.println("User saved: " + ((User) args.getNewObject()).getEvents().size());
            });
            user.setAge(30); // Example update
            user.save(false);
        }
        return null;
    }
}
