package com.euonia.bus.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents that the message is a multicast message, which means it will be sent to multiple subscribers.
 * This annotation is used to mark the message class as a multicast message, and the bus will ensure that all subscribers that are subscribed to the message type will receive the message.
 * Multicast messages are typically used for events or notifications that need to be broadcasted to multiple subscribers without expecting a response.
 * Subscribers can choose to receive multicast messages based on their subscription criteria, allowing for a flexible and scalable messaging system where messages can be distributed to multiple recipients without the need for direct connections between publishers and subscribers.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Multicast {
}
