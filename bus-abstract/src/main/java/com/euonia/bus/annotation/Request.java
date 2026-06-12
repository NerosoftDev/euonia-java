package com.euonia.bus.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents that the message is a request message, which means it will be sent to one or more subscribers and expects a response.
 * This annotation is used to mark the message class as a request message, and the bus will ensure that the subscribers can send a response back to the sender.
 * The response type is specified by the responseType property of the annotation, which indicates the type of the response that the subscribers should return when they receive the request message.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Request {
    Class<?> responseType();
}
