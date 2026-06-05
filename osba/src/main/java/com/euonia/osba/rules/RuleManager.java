package com.euonia.osba.rules;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages the rules associated with a specific type. It provides methods to retrieve and manage the rules for that type.
 * The RuleManager is designed to be thread-safe, allowing concurrent access and modifications to the rules without the need for external synchronization.
 * It uses a ConcurrentMap to store RuleManager instances for different types, and a CopyOnWriteArrayList to store the rules for each type, ensuring that modifications to the rules do not affect concurrent reads.
 */
public final class RuleManager {
    private static final ConcurrentMap<Class<?>, RuleManager> ruleSets = new java.util.concurrent.ConcurrentHashMap<>();

    private final List<Rule> rules = new CopyOnWriteArrayList<>();

    private RuleManager() {
    }

    public List<Rule> getRules() {
        return rules;
    }

    /**
     * Retrieves the RuleManager instance associated with the specified type. If no RuleManager exists for the type, a new instance is created and stored in the ruleSets map.
     *
     * @param type the class type whose RuleManager is to be retrieved
     * @return the RuleManager instance associated with the specified type
     */
    public static RuleManager getRules(Class<?> type) {
        return ruleSets.computeIfAbsent(type, c -> new RuleManager());
    }

    /**
     * Removes the RuleManager associated with the specified type from the ruleSets map, effectively clearing all rules for that type.
     *
     * @param type the class type whose rules are to be cleared
     */
    public static void cleanRules(Class<?> type) {
        synchronized (ruleSets) {
            ruleSets.remove(type);
        }
    }

    private boolean initialized = false;

    /**
     * Indicates whether the RuleManager has been initialized with rules for the associated type.
     * This property can be used to determine if the rules have been set up and are ready for use.
     *
     * @return true if the RuleManager has been initialized, false otherwise
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Sets the initialized state of the RuleManager.
     * This method can be used to mark the RuleManager as initialized after rules have been added for the associated type.
     *
     * @param initialized true to mark the RuleManager as initialized, false otherwise
     */
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }
}
