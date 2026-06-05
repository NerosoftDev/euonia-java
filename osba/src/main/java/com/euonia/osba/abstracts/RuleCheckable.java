package com.euonia.osba.abstracts;

import com.euonia.osba.rules.BrokenRuleCollection;
import com.euonia.reflection.PropertyInfo;

/**
 * Represents that the implemented class have rule checking.
 */
public interface RuleCheckable {

    /**
     * Indicates whether the object is valid based on the current broken rules.
     *
     * @return true if the object is valid, false otherwise
     */
    boolean isValid();

    void ruleCheckComplete(PropertyInfo<?> property);

    void ruleCheckComplete(String property);

    void allRulesComplete();

    /**
     * Suspends rule checking for the object.
     * While rule checking is suspended, changes to properties will not trigger rule evaluations or updates to the broken rules collection.
     * This can be useful when performing multiple updates to an object,
     * and you want to avoid intermediate rule checks until all changes are made.
     */
    void suspendRuleChecking();

    /**
     * Resumes rule checking for the object after it has been suspended.
     * When rule checking is resumed, any changes made to properties while it was suspended will trigger rule evaluations, and the broken rules collection will be updated accordingly.
     * This allows you to ensure that the object's validity is accurately reflected after making multiple changes while rule checking was suspended.
     */
    void resumeRuleChecking();

    /**
     * Gets the collection of broken rules for the object.
     *
     * @return the collection of broken rules
     */
    BrokenRuleCollection getBrokenRules();
}
