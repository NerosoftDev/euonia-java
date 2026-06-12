package com.euonia.bus.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Subscribe {
    /**
     * The channel to subscribe to.
     *
     * @return the channel name
     */
    String value();

    /**
     * Gets or sets the group name for this subscription.
     * Messages published to the same channel but with different group names will be delivered to different subscribers, while messages published to the same channel and group name will be load balanced among those subscribers.
     *
     * @return the group name for this subscription
     */
    String group() default "";
}
