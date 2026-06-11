package com.euonia.bus.convention;

import com.euonia.bus.MessageConventionType;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * MessageConventionBuilder provides a fluent API for configuring message conventions by allowing users to define custom rules for classifying message types (unicast, multicast, request) based on predicates or custom convention implementations.
 * <p>
 * This builder allows users to easily customize the message classification logic by providing methods to evaluate unicast, multicast, and request types using predicates, as well as a method to evaluate all message types using a custom function. Additionally, users can add custom MessageConvention implementations directly to the builder.
 * </p>
 * <p>
 * The resulting MessageConvention can be retrieved using the getConvention() method, which returns a BaseMessageConvention instance that aggregates all the defined conventions and applies them when classifying message types. This builder simplifies the process of configuring message conventions and promotes a flexible and extensible approach to message classification in the bus system.
 * </p>
 */
public final class MessageConventionBuilder {
    private final BaseMessageConvention convention = new BaseMessageConvention();

    public BaseMessageConvention getConvention() {
        return this.convention;
    }

    /**
     * Adds a message convention that will be used to evaluate whether a type is a unicast message.
     *
     * @param predicate the predicate to evaluate the unicast message type
     * @return the current instance of MessageConventionBuilder
     */
    public MessageConventionBuilder evaluateUnicast(Predicate<Class<?>> predicate) {
        assert predicate != null : "predicate cannot be null.";
        this.convention.defineUnicastTypeConvention(predicate);
        return this;
    }

    /**
     * Adds a message convention that will be used to evaluate whether a type is a multicast message.
     *
     * @param predicate the predicate to evaluate the multicast message type
     * @return the current instance of MessageConventionBuilder
     */
    public MessageConventionBuilder evaluateMulticast(Predicate<Class<?>> predicate) {
        assert predicate != null : "predicate cannot be null.";
        this.convention.defineMulticastTypeConvention(predicate);
        return this;
    }

    /**
     * Adds a message convention that will be used to evaluate whether a type is a request message.
     *
     * @param predicate the predicate to evaluate the request message type
     * @return the current instance of MessageConventionBuilder
     */
    public MessageConventionBuilder evaluateRequest(Predicate<Class<?>> predicate) {
        assert predicate != null : "predicate cannot be null.";
        this.convention.defineRequestTypeConvention(predicate);
        return this;
    }

    /**
     * Adds a message convention that will be used to evaluate whether a type is a unicast, multicast, or request message.
     *
     * @param convention the function to evaluate the message type
     * @return the current instance of MessageConventionBuilder
     */
    public MessageConventionBuilder evaluate(Function<Class<?>, MessageConventionType> convention) {
        assert convention != null : "convention cannot be null.";
        this.convention.defineTypeConvention(convention);
        return this;
    }

    /**
     * Adds a message convention that will be used to evaluate whether a type is a unicast, multicast, or request message.
     *
     * @param convention the MessageConvention instance to add
     * @param <C>        the type of the MessageConvention
     * @return the current instance of MessageConventionBuilder
     */
    public <C extends MessageConvention> MessageConventionBuilder add(C convention) {
        assert convention != null : "convention cannot be null.";
        this.convention.add(convention);
        return this;
    }

    /**
     * Adds a message convention that will be used to evaluate whether a type is a unicast, multicast, or request message.
     * The convention will be instantiated using its default constructor.
     *
     * @param conventionClass the class of the MessageConvention to add
     * @param <C>             the type of the MessageConvention
     * @return the current instance of MessageConventionBuilder
     */
    public <C extends MessageConvention> MessageConventionBuilder add(Class<C> conventionClass) {
        assert conventionClass != null : "conventionClass cannot be null.";
        try {
            C convention = conventionClass.getDeclaredConstructor().newInstance();
            this.convention.add(convention);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate convention: " + conventionClass, e);
        }
        return this;
    }
}
