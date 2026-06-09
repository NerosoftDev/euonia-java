package com.euonia.osba.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Represents a collection of broken rules that have been violated during rule checking.
 * It provides methods to manage the collection of broken rules, including adding new broken rules, clearing existing rules, and retrieving counts of broken rules based on their severity.
 */
public final class BrokenRuleCollection {
    private final List<BrokenRule> rules = new ArrayList<>();

    /**
     * Clears all broken rules from the collection, regardless of their associated property.
     */
    public synchronized void clearRules() {
        rules.clear();
    }

    /**
     * Clears broken rules from the collection that are associated with the specified property name.
     *
     * @param propertyName the name of the property whose associated broken rules should be cleared
     */
    public synchronized void clearRules(String propertyName) {
        rules.removeIf(rule -> propertyName == null
            ? rule.getProperty() == null
            : propertyName.equals(rule.getProperty()));
    }

    /**
     * Adds new broken rules to the collection based on the provided list of rule results and the associated property name.
     *
     * @param results      the list of rule results to be added as broken rules
     * @param propertyName the name of the property associated with the broken rules
     */
    public synchronized void add(List<RuleResult> results, String propertyName) {
        for (var result : results) {
            if (result.isSuccess()) {
                continue;
            }
            rules.add(new BrokenRule(propertyName, result.getDescription(), result.getSeverity()));
        }
    }

    /**
     * Retrieves the count of broken rules in the collection that have a severity level of ERROR.
     *
     * @return the count of broken rules with severity ERROR
     */
    public synchronized int getErrorCount() {
        return (int) rules.stream().filter(rule -> rule.getSeverity() == RuleSeverity.ERROR).count();
    }

    /**
     * Retrieves the count of broken rules in the collection that have a severity level of WARNING.
     *
     * @return the count of broken rules with severity WARNING
     */
    public synchronized int getWarningCount() {
        return (int) rules.stream().filter(rule -> rule.getSeverity() == RuleSeverity.WARNING).count();
    }

    /**
     * Retrieves the count of broken rules in the collection that have a severity level of INFORMATION.
     *
     * @return the count of broken rules with severity INFORMATION
     */
    public synchronized int getInformationCount() {
        return (int) rules.stream().filter(rule -> rule.getSeverity() == RuleSeverity.INFORMATION).count();
    }

    /**
     * Checks if the collection of broken rules is empty.
     *
     * @return true if the collection is empty, false otherwise
     */
    public synchronized boolean isEmpty() {
        return rules.isEmpty();
    }

    /**
     * Returns an unmodifiable list of broken rules in the collection.
     *
     * @return an unmodifiable list of broken rules
     */
    public synchronized List<BrokenRule> list() {
        return List.copyOf(rules);
    }

    /**
     * Returns a stream of broken rules in the collection.
     *
     * @return a stream of broken rules
     */
    public synchronized Stream<BrokenRule> stream() {
        return new ArrayList<>(rules).stream();
    }
}
