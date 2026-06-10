package com.euonia.bus.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Represents a composite transport strategy that aggregates multiple {@link TransportStrategy} instances
 * and caches the results of outgoing and incoming message type evaluations using {@link ConcurrentHashMap}.
 * <p>
 * Each message type (outgoing, incoming) has its own dedicated cache backed by {@code ConcurrentHashMap&lt;Class&lt;?&gt;, Boolean&gt;}. Since {@link Class} inherits identity-based {@code hashCode()} / {@code equals()} from {@link Object}, the cache key behaves equivalently to .NET's {@code RuntimeTypeHandle} — lightweight, pointer-level comparison with no heap allocation overhead per lookup.
 * <p>
 * The composite strategy allows for flexible configuration by enabling the addition of custom transport strategies and the definition of custom predicates for determining outgoing and incoming message types. The default strategy can be overridden to provide custom logic for classifying message types without needing to implement a full {@link TransportStrategy}.
 * <p> This design promotes extensibility and performance by leveraging caching and a modular approach to strategy composition.
 */
public class BaseTransportStrategy implements TransportStrategy {
    private final OverridableTransportStrategy defaultStrategy = new OverridableTransportStrategy(new DefaultTransportStrategy());
    private final List<TransportStrategy> strategies = new ArrayList<>();

    private final StrategyCache outgoingCache = new StrategyCache();
    private final StrategyCache incomingCache = new StrategyCache();

    public BaseTransportStrategy() {
        strategies.add(defaultStrategy);
    }

    @Override
    public String getName() {
        return "Composite transport strategy";
    }

    @Override
    public boolean outgoing(Class<?> messageType) {
        assert messageType != null : "messageType cannot be null.";
        return outgoingCache.apply(messageType, type -> strategies.stream().anyMatch(s -> s.outgoing(type)));
    }

    @Override
    public boolean incoming(Class<?> messageType) {
        assert messageType != null : "messageType cannot be null.";
        return incomingCache.apply(messageType, type -> strategies.stream().anyMatch(s -> s.incoming(type)));
    }

    /**
     * Adds one or more transport strategies to the composite strategy.
     * The added strategies will be evaluated in order when determining if a message type is outgoing or incoming.
     *
     * @param strategies the transport strategies to add
     */
    void add(TransportStrategy... strategies) {
        if (strategies == null || strategies.length == 0) {
            throw new IllegalArgumentException("strategies cannot be null or empty.");
        }
        Collections.addAll(this.strategies, strategies);
    }

    /**
     * Defines a custom strategy for evaluating outgoing message types.
     *
     * @param strategy a predicate that determines if a message type is considered outgoing
     */
    void defineOutgoingStrategy(Predicate<Class<?>> strategy) {
        defaultStrategy.setOutgoingPredicate(strategy);
    }

    /**
     * Defines a custom strategy for evaluating incoming message types.
     *
     * @param strategy a predicate that determines if a message type is considered incoming
     */
    void defineIncomingStrategy(Predicate<Class<?>> strategy) {
        defaultStrategy.setIncomingPredicate(strategy);
    }

    /**
     * Resets the caches for outgoing and incoming message evaluations.
     */
    void resetCache() {
        outgoingCache.reset();
        incomingCache.reset();
    }

    /**
     * StrategyCache is a helper class that manages a cache for message type evaluations.
     * It uses a {@link ConcurrentHashMap} to store the results of evaluating whether a message type is outgoing or incoming according to the provided strategy.
     * The cache allows for fast lookups while ensuring thread safety in concurrent environments.
     */
    private static class StrategyCache {
        private final ConcurrentHashMap<Class<?>, Boolean> cache = new ConcurrentHashMap<>();

        boolean apply(Class<?> messageType, Predicate<Class<?>> strategy) {
            return cache.computeIfAbsent(messageType, strategy::test);
        }

        void reset() {
            cache.clear();
        }
    }
}
