package com.euonia.osba.rules;

import com.euonia.osba.abstracts.RuleCheckable;
import com.euonia.reflection.PropertyInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

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

    public void addRule(Rule rule) {
        if (rule != null) {
            getRuleManager().getRules().add(rule);
        }
    }

    public BrokenRuleCollection getBrokenRules() {
        return brokenRules;
    }

    public boolean isValid() {
        return brokenRules.getErrorCount() == 0;
    }

    public boolean hasRunningRules() {
        return hasRunningRules;
    }

    public void setSuppressRuleChecking(boolean suppressRuleChecking) {
        this.suppressRuleChecking = suppressRuleChecking;
    }

    public void addValidationCompleteListener(Consumer<BrokenRuleCollection> listener) {
        if (listener != null) {
            validationCompleteListeners.add(listener);
        }
    }

    public void removeValidationCompleteListener(Consumer<BrokenRuleCollection> listener) {
        validationCompleteListeners.remove(listener);
    }

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
