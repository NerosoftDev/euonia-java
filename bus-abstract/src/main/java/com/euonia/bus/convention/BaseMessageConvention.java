package com.euonia.bus.convention;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.euonia.bus.MessageConventionType;

/**
 * The built-in message convention that aggregates multiple {@link MessageConvention} instances
 * and caches type classification results using {@link ConcurrentHashMap}.
 * <p>
 * Each message type (unicast, multicast, request) has its own dedicated cache backed by
 * {@code ConcurrentHashMap&lt;Class&lt;?&gt;, Boolean&gt;}. Since {@link Class} inherits
 * identity-based {@code hashCode()} / {@code equals()} from {@link Object}, the cache key
 * behaves equivalently to .NET's {@code RuntimeTypeHandle} — lightweight, pointer-level
 * comparison with no heap allocation overhead per lookup.
 */
public class BaseMessageConvention implements MessageConvention {

    private static final String DEFAULT_NAME = "Default";

    private final OverridableMessageConvention defaultConvention =
            new OverridableMessageConvention(new DefaultMessageConvention());

    private final List<MessageConvention> conventions = new ArrayList<>();

    private final ConventionCache unicastConventionCache = new ConventionCache();
    private final ConventionCache multicastConventionCache = new ConventionCache();
    private final ConventionCache requestConventionCache = new ConventionCache();

    public BaseMessageConvention() {
        conventions.add(defaultConvention);
    }

    /**
     * Determines whether the specified type is a unicast message.
     *
     * @param messageType the type to check; must not be {@code null}
     * @return {@code true} if any registered convention classifies this type as unicast
     * @throws NullPointerException if {@code messageType} is {@code null}
     */
    @Override
    public boolean isUnicastType(Class<?> messageType) {
        Objects.requireNonNull(messageType, "messageType cannot be null.");

        return unicastConventionCache.apply(messageType,
                type -> conventions.stream().anyMatch(c -> c.isUnicastType(type)));
    }

    /**
     * Determines whether the specified type is a multicast message.
     *
     * @param messageType the type to check; must not be {@code null}
     * @return {@code true} if any registered convention classifies this type as multicast
     * @throws NullPointerException if {@code messageType} is {@code null}
     */
    @Override
    public boolean isMulticastType(Class<?> messageType) {
        Objects.requireNonNull(messageType, "messageType cannot be null.");

        return multicastConventionCache.apply(messageType,
                type -> conventions.stream().anyMatch(c -> c.isMulticastType(type)));
    }

    /**
     * Determines whether the specified type is a request message.
     *
     * @param messageType the type to check; must not be {@code null}
     * @return {@code true} if any registered convention classifies this type as request
     * @throws NullPointerException if {@code messageType} is {@code null}
     */
    @Override
    public boolean isRequestType(Class<?> messageType) {
        Objects.requireNonNull(messageType, "messageType cannot be null.");

        return requestConventionCache.apply(messageType,
                type -> conventions.stream().anyMatch(c -> c.isRequestType(type)));
    }

    @Override
    public String getName() {
        return DEFAULT_NAME;
    }

    // ---- Convention definition methods (package-private, used by configuration) ----

    void defineUnicastTypeConvention(Predicate<Class<?>> convention) {
        defaultConvention.setUnicastPredicate(convention);
    }

    void defineMulticastTypeConvention(Predicate<Class<?>> convention) {
        defaultConvention.setMulticastPredicate(convention);
    }

    void defineRequestTypeConvention(Predicate<Class<?>> convention) {
        defaultConvention.setRequestPredicate(convention);
    }

    /**
     * Defines a unified type convention that maps a message type to its
     * {@link MessageConventionType}, then splits the logic into separate
     * unicast, multicast, and request predicates.
     *
     * @param convention a function that classifies a type into a convention category
     */
    void defineTypeConvention(Function<Class<?>, MessageConventionType> convention) {
        Objects.requireNonNull(convention, "convention cannot be null.");

        defineUnicastTypeConvention(type -> convention.apply(type) == MessageConventionType.UNICAST);
        defineMulticastTypeConvention(type -> convention.apply(type) == MessageConventionType.MULTICAST);
        defineRequestTypeConvention(type -> convention.apply(type) == MessageConventionType.REQUEST);
    }

    void add(MessageConvention... conventions) {
        if (conventions == null || conventions.length == 0) {
            throw new IllegalArgumentException("At least one convention must be provided.");
        }
        this.conventions.addAll(Arrays.asList(conventions));
    }

    /**
     * Returns the names of all registered conventions (for diagnostic purposes).
     */
    String[] getRegisteredConventions() {
        return conventions.stream()
                .map(MessageConvention::getName)
                .toList()
                .toArray(new String[0]);
    }

    // ---- Inner cache class ----

    /**
     * A thread-safe, type-keyed cache that computes a value on first access
     * and reuses it for all subsequent lookups of the same key.
     * <p>
     * Uses {@link Class} as the key, which relies on identity-based
     * {@code hashCode()} / {@code equals()} — equivalent in spirit to
     * .NET's {@code RuntimeTypeHandle}-based caching.
     */
    private static class ConventionCache {

        private final ConcurrentHashMap<Class<?>, Boolean> cache = new ConcurrentHashMap<>();

        /**
         * Returns the cached result for the given type, or computes and stores it
         * on first access using the provided function.
         *
         * @param type       the class to look up or compute
         * @param convention the function to compute the result on cache miss
         * @return the cached or newly computed boolean value
         */
        boolean apply(Class<?> type, Predicate<Class<?>> convention) {
            return cache.computeIfAbsent(type, convention::test);
        }

        /**
         * Clears all cached entries.
         */
        void reset() {
            cache.clear();
        }
    }
}
