package com.euonia.osba.rules;

import com.euonia.osba.abstracts.RuleCheckable;
import com.euonia.reflection.PropertyInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Rules manages the execution of validation rules for a business object, tracking broken rules and notifying listeners when validation is complete.
 */
public final class Rules {
    private final RuleCheckable target;
    private final List<Consumer<BrokenRuleCollection>> validationCompleteListeners = new CopyOnWriteArrayList<>();
    private final BrokenRuleCollection brokenRules = new BrokenRuleCollection();
    private final List<Rule> runningRules = new CopyOnWriteArrayList<>();

    private volatile boolean suppressRuleChecking;
    private volatile boolean hasRunningRules;

    public Rules(RuleCheckable target) {
        this.target = target;
    }

    private volatile RuleManager ruleManager;

    /**
     * Gets the RuleManager for the target object, initializing it if necessary.
     *
     * @return the RuleManager for the target object
     */
    public RuleManager getRuleManager() {
        if (ruleManager == null) {
            synchronized (this) {
                if (ruleManager == null) {
                    ruleManager = RuleManager.getRules(target.getClass());
                }
            }
        }
        return ruleManager;
    }

    /**
     * Adds a validation rule to the target object's RuleManager.
     *
     * @param rule the validation rule to add
     */
    public void addRule(Rule rule) {
        if (rule != null) {
            getRuleManager().getRules().add(rule);
        }
    }

    /**
     * Gets the collection of broken rules that have been identified during rule checking.
     *
     * @return the collection of broken rules
     */
    public BrokenRuleCollection getBrokenRules() {
        return brokenRules;
    }

    /**
     * Determines whether the target object is valid based on the current collection of broken rules.
     *
     * @return true if the target object is valid, false otherwise
     */
    public boolean isValid() {
        return brokenRules.getErrorCount() == 0;
    }

    /**
     * Checks whether there are currently any rules running for the target object.
     *
     * @return true if there are rules running, false otherwise
     */
    public boolean hasRunningRules() {
        return hasRunningRules;
    }

    /**
     * Gets whether rule checking is currently suppressed for the target object.
     * When rule checking is suppressed, no rules will be executed and the object will be considered valid.
     *
     * @return true if rule checking is suppressed, false otherwise
     */
    public boolean isSuppressRuleChecking() {
        return suppressRuleChecking;
    }

    /**
     * Sets whether rule checking is currently suppressed for the target object.
     * When rule checking is suppressed, no rules will be executed and the object will be considered valid.
     *
     * @param suppressRuleChecking true to suppress rule checking, false to enable it
     */
    public void setSuppressRuleChecking(boolean suppressRuleChecking) {
        this.suppressRuleChecking = suppressRuleChecking;
    }

    /**
     * Adds a listener to be notified when validation is complete.
     * The listener will receive the collection of broken rules as an argument.
     *
     * @param listener the listener to be added
     */
    public void addValidationCompleteListener(Consumer<BrokenRuleCollection> listener) {
        if (listener != null) {
            validationCompleteListeners.add(listener);
        }
    }

    /**
     * Removes a listener from being notified when validation is complete.
     *
     * @param listener the listener to be removed
     */
    public void removeValidationCompleteListener(Consumer<BrokenRuleCollection> listener) {
        validationCompleteListeners.remove(listener);
    }

    /**
     * Asynchronously checks all validation rules for the target object and updates the collection of broken rules accordingly.
     * The method returns a CompletableFuture that completes when all rules have been checked, providing a list of affected property names.
     *
     * @return a CompletableFuture that completes with a list of affected property names
     */
    public CompletableFuture<List<String>> checkObjectRulesAsync() {
        if (suppressRuleChecking || getRuleManager().getRules().isEmpty()) {
            return CompletableFuture.completedFuture(List.of());
        }

        hasRunningRules = true;
        brokenRules.clearRules();
        List<String> affectedProperties = new ArrayList<>();
        List<CompletableFuture<Void>> tasks = getRuleManager().getRules().stream()
                                                              .sorted(Comparator.comparingInt(Rule::getPriority))
                                                              .map(rule -> runRule(rule, affectedProperties))
                                                              .toList();

        return CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new))
                                .thenApply(ignored -> affectedProperties.stream().distinct().toList())
                                .whenComplete((ignored, error) -> {
                                    hasRunningRules = false;
                                    notifyValidationComplete();
                                });
    }

    private CompletableFuture<Void> runRule(Rule rule, List<String> affectedProperties) {
        RuleContext context = new RuleContext(ctx -> {
            synchronized (this) {
                getBrokenRules().add(ctx.getResults(), ctx.getRule().getProperty().getName());
                runningRules.remove(ctx.getRule());

                var properties = new ArrayList<PropertyInfo<?>>();

                if (ctx.getRule().getProperty() != null) {
                    properties.add(ctx.getRule().getProperty());
                }

                if (ctx.getRule().getRelatedProperties() != null) {
                    properties.addAll(ctx.getRule().getRelatedProperties());
                }

                for (var property : properties) {
                    if (runningRules.stream().noneMatch(r -> r.getName().equals(property.getName()))) {
                        target.ruleCheckComplete(property);
                    }
                }

                if (!hasRunningRules) {
                    target.allRulesComplete();
                }
            }
        }) {{
            setRule(rule);
            setTarget(target);
            if(rule.getProperty() == null){
                System.out.println(rule.getName());
            }
            setPropertyName(rule.getProperty().getName());
        }};
        String propertyName = rule.getProperty().getName();
        if (propertyName != null) {
            brokenRules.clearRules(propertyName);
            affectedProperties.add(propertyName);
        }
        affectedProperties.addAll(rule.getRelatedProperties().stream().map(PropertyInfo::getName).toList());

        runningRules.add(rule);
        return rule.executeAsync(context)
                   .thenRun(context::complete);
    }

    private void notifyValidationComplete() {
        for (var listener : validationCompleteListeners) {
            listener.accept(brokenRules);
        }
    }
}
