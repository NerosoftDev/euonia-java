package com.euonia.bus.recipient;

public interface Recipient extends AutoCloseable {

    /**
     * Gets the unique name of this recipient.
     *
     * @return the name of this recipient; must not be {@code null}
     */
    String getName();

}
