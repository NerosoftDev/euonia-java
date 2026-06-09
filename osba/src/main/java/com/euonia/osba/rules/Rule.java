package com.euonia.osba.rules;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.euonia.reflection.PropertyInfo;

/**
 * Represents a rule that is based on a specific data annotation. This rule can
 * be used to enforce validation or other logic based on the presence and
 * configuration of the specified annotation on a property.
 *
 * @param <A> the type of the annotation that this rule is based on
 */
public interface Rule {
    /**
     * Gets the name of the rule, which is a unique identifier for the rule. The
     * name can be used to reference the rule in various contexts, such as when
     * applying the rule to a business object or when logging rule execution.
     *
     * @return the name of the rule, which is a unique identifier for the rule
     */
    String getName();

    /**
     * Gets the property associated with this rule, which is the primary property
     * that the rule is designed to evaluate or enforce.
     * This property serves as the main focus of the rule's logic and can be used to
     * determine when the rule should be applied and how it should be evaluated.
     *
     * @return the property associated with this rule, which is the primary property
     *         that the rule is designed to evaluate or enforce
     */
    PropertyInfo<?> getProperty();

    /**
     * Gets a list of related properties that are associated with this rule. This
     * can be used to identify additional properties that may be relevant when
     * evaluating the rule, allowing for more complex and interconnected rule logic.
     *
     * @return a list of related properties that are associated with this rule,
     *         which may be used in the evaluation of the rule's logic
     */
    default List<PropertyInfo<?>> getRelatedProperties() {
        return List.of();
    }

    /**
     * Gets the priority of the rule, which can be used to determine the order of
     * execution when multiple rules are applied. Higher priority rules will be
     * executed before lower priority ones.
     *
     * @return the priority of the rule, with higher values indicating higher
     *         priority
     */
    int getPriority();

    /**
     * Executes the rule asynchronously, performing the necessary checks or actions
     * based on the provided context. The implementation of this method should
     * contain the logic for evaluating the rule and potentially modifying the
     * context or throwing exceptions if the rule is violated.
     *
     * @param context the context in which the rule is being executed, containing
     *                relevant information and state for the rule evaluation
     * @return a CompletableFuture that completes when the rule execution is
     *         finished, allowing for asynchronous processing and handling of the
     *         rule's effects on the context
     */
    CompletableFuture<Void> executeAsync(RuleContext context);
}
