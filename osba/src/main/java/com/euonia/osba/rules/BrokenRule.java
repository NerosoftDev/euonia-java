package com.euonia.osba.rules;

/**
 * Represents a broken rule that has been violated during rule checking.
 * It contains information about the property that violated the rule, a description of the violation, and the severity of the violation.
 */
public final class BrokenRule {
    private final String property;
    private final String description;
    private final RuleSeverity severity;

    public BrokenRule(String property, String description, RuleSeverity severity) {
        this.property = property;
        this.description = description;
        this.severity = severity;
    }

    public String getProperty() {
        return property;
    }

    public String getDescription() {
        return description;
    }

    public RuleSeverity getSeverity() {
        return severity;
    }
}
