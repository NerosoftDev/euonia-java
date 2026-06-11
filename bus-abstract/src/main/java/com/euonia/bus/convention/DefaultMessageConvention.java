package com.euonia.bus.convention;

import com.euonia.bus.contract.Queue;
import com.euonia.bus.contract.Request;
import com.euonia.bus.contract.Topic;

/**
 * DefaultMessageConvention is a simple implementation of the MessageConvention interface that uses class type checks to determine the message type.
 * It considers any class that implements Queue (but is not Queue itself) as a unicast message, any class that implements Topic (but is not Topic itself) as a multicast message, and any class that implements Request (but is not Request itself) as a request message.
 * This convention is straightforward and relies on the class hierarchy to determine the message type, making it easy to use and understand. It is suitable for simple scenarios where the message types can be easily distinguished by their class structure.
 */
public class DefaultMessageConvention implements MessageConvention {
    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean isUnicastType(Class<?> messageType) {
        return Queue.class.isAssignableFrom(messageType) && messageType != Queue.class;
    }

    @Override
    public boolean isMulticastType(Class<?> messageType) {
        return Topic.class.isAssignableFrom(messageType) && messageType != Topic.class;
    }

    @Override
    public boolean isRequestType(Class<?> messageType) {
        return Request.class.isAssignableFrom(messageType) && messageType != Request.class;
    }
}
