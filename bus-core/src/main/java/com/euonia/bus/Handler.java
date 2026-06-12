package com.euonia.bus;

/**
 * Defines a contract for handling messages of a specific type and returning a response.
 *
 * @param <M> the type of the message to be handled
 * @param <R> the type of the response to be returned after handling the message
 */
public interface Handler<M, R> {
    /**
     * Handles a message of type M and returns a response of type R.
     *
     * @param message the message to be handled
     * @param context the context in which the message is being handled
     * @return the response after handling the message
     */
    R handle(M message, MessageContext context);
}
