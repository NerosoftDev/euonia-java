package com.euonia.security;

import java.util.Collections;
import java.util.Map;

/**
 * Exception thrown for account-related errors during authentication or authorization processes, such as account not found, account locked, etc.
 * This exception can be extended to provide more specific error types and include additional details as needed.
 */
@SuppressWarnings("unused")
public abstract class AccountException extends RuntimeException {
    private final Object identity;

    private final Map<String, Object> details = Collections.emptyMap();

    /**
     * Creates a new AccountException with the specified identity. The identity can be any object that represents the account, such as a username, user ID, or email address. This constructor allows for creating an exception without a specific message or cause, while still providing context about which account is involved in the error.
     *
     * @param identity The identity associated with the account error, such as username or user ID.
     */
    public AccountException(Object identity) {
        this.identity = identity;
    }

    /**
     * Creates a new AccountException with the specified identity and detail message. This constructor allows for creating an exception with a specific message while still providing context about which account is involved in the error.
     *
     * @param identity The identity associated with the account error, such as username or user ID.
     * @param message The detail message explaining the reason for the exception.
     */
    public AccountException(Object identity, String message) {
        super(message);
        this.identity = identity;
    }

    /**
     * Creates a new AccountException with the specified identity, detail message, and cause. This constructor allows for creating an exception with a specific message and cause while still providing context about which account is involved in the error.
     *
     * @param identity The identity associated with the account error, such as username or user ID.
     * @param message The detail message explaining the reason for the exception.
     * @param cause The cause of the exception (which is saved for later retrieval by the getCause() method).
     */
    public AccountException(Object identity, String message, Throwable cause) {
        super(message, cause);
        this.identity = identity;
    }

    /**
     * Gets the identity associated with the account error. This can be used to provide more context about the error, such as which username or user ID was involved.
     *
     * @return The identity associated with the account error.
     */
    public Object getIdentity() {
        return identity;
    }

    /**
     * Gets additional details related to the account exception. This can be used to provide more context about the error, such as which field was invalid or what the expected format was.
     *
     * @return A map of additional details related to the account exception.
     */
    public Map<String, Object> getDetails() {
        return details;
    }

    /**
     * Gets a specific detail from the details map based on the provided key. This can be used to retrieve specific information about the account error, such as which field was invalid or what the expected format was.
     *
     * @param key The key for the detail to retrieve.
     * @return The value associated with the specified key in the details map, or null if the key does not exist.
     */
    public Object get(String key) {
        return details.getOrDefault(key, null);
    }

    /**
     * Sets a specific detail in the details map with the provided key and value. This can be used to add additional context about the account error, such as which field was invalid or what the expected format was.
     *
     * @param key The key for the detail.
     * @param value The value for the detail.
     */
    public void set(String key, Object value) {
        details.put(key, value);
    }

    /**
     * Sets a specific detail in the details map with the provided key and value, and returns the AccountException instance for method chaining. This can be used to add additional context about the account error while allowing for fluent API style when constructing the exception.
     *
     * @param key The key for the detail.
     * @param value The value for the detail.
     * @return The AccountException instance, allowing for method chaining.
     */
    public AccountException with(String key, Object value) {
        details.put(key, value);
        return this;
    }
}
