package com.euonia.bus.contract;

/**
 * Unicast is a marker interface to indicate that a message is intended for a single recipient.
 * Messages that implement this interface will be treated as unicast messages, meaning they will be delivered to only one subscriber, even if multiple subscribers are registered for the same message type.
 * This is useful for scenarios where a message should be processed by only one handler, such as commands or requests that require a single response.
 */
public interface Unicast {
}
