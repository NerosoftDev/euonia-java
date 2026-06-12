package com.euonia.bus.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents that the message is a unicast message, which means it will be sent to only one subscriber.
 * This annotation is used to mark the message class as a unicast message, and the bus will ensure that only one subscriber receives the message.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Unicast {
}
