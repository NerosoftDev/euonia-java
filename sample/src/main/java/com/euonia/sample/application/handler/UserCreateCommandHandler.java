package com.euonia.sample.application.handler;

import com.euonia.bus.Handler;
import com.euonia.bus.MessageContext;
import com.euonia.factory.ObjectFactory;
import com.euonia.sample.application.command.UserCreateCommand;
import com.euonia.sample.domain.aggregate.User;
import com.euonia.spring.BeanScope;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanScope.PROTOTYPE)
public class UserCreateCommandHandler implements Handler<UserCreateCommand, Void> {

    private final ObjectFactory factory;

    public UserCreateCommandHandler(ObjectFactory factory) {
        this.factory = factory;
    }

    @Override
    public Void handle(UserCreateCommand message, MessageContext context) {
        var user = factory.create(User.class, message.getName());
        try (user) {
            user.onSaved((args) -> {
                System.out.println("User saved: " + ((User) args.getNewObject()).getEvents().size());
            });
            user.setAge(20);
            user.save(false);
            //return ResponseEntity.ok("User created with name: " + user.getName());
            context.response(user.getId());
        }
        return null;
    }
}
