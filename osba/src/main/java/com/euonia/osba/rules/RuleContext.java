package com.euonia.osba.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class RuleContext {
    private final List<RuleResult> results = new ArrayList<>();
    private final Consumer<RuleContext> completeAction;
    private Rule rule;
    private Object target;
    private String propertyName;

    public RuleContext(Consumer<RuleContext> completeAction) {
        this.completeAction = completeAction;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public List<RuleResult> getResults() {
        return Collections.unmodifiableList(results);
    }

    public void addErrorResult(String description) {
        results.add(new RuleResult(rule.getName(), description, RuleSeverity.ERROR));
    }

    public void addWarningResult(String description) {
        results.add(new RuleResult(rule.getName(), description, RuleSeverity.WARNING));
    }

    public void addInformationResult(String description) {
        results.add(new RuleResult(rule.getName(), description, RuleSeverity.INFORMATION));
    }

    public void addSuccessResult() {
        results.add(new RuleResult(rule.getName()));
    }

    public void complete() {
        if (results.isEmpty()) {
            addSuccessResult();
        }
        if (completeAction != null) {
            completeAction.accept(this);
        }
    }
}
