package com.euonia.bus.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a message that should be enqueued for processing. The value of the annotation specifies the queue name.
 * The priority attribute can be used to indicate the processing priority of the message, with higher values indicating higher priority.
 * This annotation is typically used in conjunction with a message bus or queueing system to ensure that messages are processed in the correct order and with the appropriate level of urgency.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Enqueue {
    String value();

    /**
     * Indicates the processing priority of the message, with higher values indicating higher priority. The default value is 0.
     * @return the priority level of the message
     */
    int priority() default 0;
}
