package com.euonia.utility;

import java.util.function.Supplier;

public class StringUtility {
    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static String decapitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toLowerCase() + input.substring(1);
    }

    public static String capitalizeFirstLetterWithUnderscore(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static String collapse(String... parts) {
        for (String part : parts) {
            if (part != null && !part.isEmpty()) {
                return part;
            }
        }
        return null;
    }

    @SafeVarargs
    public static String collapse(Supplier<String>... suppliers) {
        for (Supplier<String> supplier : suppliers) {
            String value = supplier.get();
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        return null;
    }
}
