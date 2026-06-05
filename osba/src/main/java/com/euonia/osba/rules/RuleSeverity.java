package com.euonia.osba.rules;

/**
 * Defines the severity levels for rules in the OSBA framework.
 * These levels indicate the importance of a rule violation and can be used to categorize and prioritize issues found during rule evaluation.
 */
public enum RuleSeverity {
    /**
     * Indicates a critical issue that must be addressed immediately.
     * This level is used for violations that could lead to significant problems or failures if not resolved.
     */
    ERROR,
    /**
     * Indicates a warning that should be addressed but is not critical.
     * This level is used for violations that could lead to potential issues if not resolved.
     */
    WARNING,
    /**
     * Indicates informational messages that do not require immediate action.
     * This level is used for violations that provide insights or recommendations.
     */
    INFORMATION,
    /**
     * Indicates a successful operation or validation.
     * This level is used for violations that confirm correct behavior or compliance.
     */
    SUCCESS
}
