package com.euonia.bus.strategy;

import java.util.function.Predicate;

public class TransportStrategyBuilder {
    private final BaseTransportStrategy strategy = new BaseTransportStrategy();

    public TransportStrategy getStrategy() {
        return strategy;
    }

    public TransportStrategyBuilder evaluateOutgoing(Predicate<Class<?>> predicate) {
        assert predicate != null : "Predicate cannot be null";
        strategy.defineOutgoingStrategy(predicate);
        return this;
    }

    public TransportStrategyBuilder evaluateIncoming(Predicate<Class<?>> predicate) {
        assert predicate != null : "Predicate cannot be null";
        strategy.defineIncomingStrategy(predicate);
        return this;
    }

    public <S extends TransportStrategy> TransportStrategyBuilder add(S strategy) {
        assert strategy != null : "Strategy cannot be null";
        this.strategy.add(strategy);
        return this;
    }

    public <S extends TransportStrategy> TransportStrategyBuilder add(Class<S> strategyType) {
        assert strategyType != null : "Strategy type cannot be null";
        try {
            S strategy = strategyType.getDeclaredConstructor().newInstance();
            this.strategy.add(strategy);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate strategy of type: " + strategyType.getName(), e);
        }
        return this;
    }
}
