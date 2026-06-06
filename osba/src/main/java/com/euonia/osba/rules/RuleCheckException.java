package com.euonia.osba.rules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleCheckException extends RuntimeException {

    private final Map<String, List<String>> errors = new HashMap<>();

    private final static String MESSAGE = "Object not valid for save.";

    public RuleCheckException(Map<String, List<String>> errors) {
        super(MESSAGE);
        this.errors.putAll(errors);
    }

    public Map<String, List<String>> getErrors() {
        return Map.copyOf(errors);
    }
}
