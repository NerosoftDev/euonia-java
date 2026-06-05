package com.euonia.osba.rules;

import com.euonia.reflection.PropertyInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the result of a rule execution, including whether the rule passed or failed,
 * a description of the result, the severity of the result, and the name of the rule that was executed.
 */
public final class RuleResult {
    private final boolean success;
    private final String description;
    private final RuleSeverity severity;
    private final String ruleName;
    private final List<PropertyInfo<?>> properties = new ArrayList<PropertyInfo<?>>();

    public RuleResult(String ruleName) {
        this(ruleName, null, RuleSeverity.SUCCESS);
    }

    public RuleResult(String ruleName, String description, RuleSeverity severity) {
        this.ruleName = ruleName;
        this.description = description;
        this.severity = severity == null ? RuleSeverity.SUCCESS : severity;
        this.success = description == null || description.isBlank();
    }

    /**
     * Indicates whether the rule execution was successful.
     * A rule is considered successful if there is no description of a failure (i.e., the description is null or blank).
     *
     * @return true if the rule execution was successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Gets the description of the rule execution result. If the rule execution was successful, this may be null or blank.
     * If the rule execution failed, this should contain a description of the failure.
     *
     * @return the description of the rule execution result
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the severity of the rule execution result.
     * The severity indicates the level of importance or impact of the rule execution result, such as success, warning, or error.
     *
     * @return the severity of the rule execution result
     */
    public RuleSeverity getSeverity() {
        return severity;
    }

    /**
     * Gets the name of the rule that was executed to produce this result.
     *
     * @return the name of the rule that was executed
     */
    public String getRuleName() {
        return ruleName;
    }

    /**
     * Gets the list of properties that are related to this rule result.
     * This can be used to identify which properties were affected by the rule execution.
     *
     * @return the list of properties related to this rule result
     */
    public List<PropertyInfo<?>> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    /**
     * Sets the list of properties that are related to this rule result.
     * This can be used to identify which properties were affected by the rule execution.
     *
     * @param properties the list of properties to set
     */
    public void setProperties(List<PropertyInfo<?>> properties) {
        this.properties.clear();
        this.properties.addAll(properties);
    }
}
