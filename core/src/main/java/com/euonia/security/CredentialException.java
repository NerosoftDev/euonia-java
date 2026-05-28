package com.euonia.security;

import java.util.Collections;
import java.util.Map;

/**
 * The CredentialException is thrown when there is an issue with user credentials, such as invalid username or password.
 * This exception indicates that the provided credentials are incorrect or do not meet the required criteria.
 */
@SuppressWarnings("unused")
public abstract class CredentialException extends RuntimeException {
    private final Object credential;
    private final Map<String, Object> details = Collections.emptyMap();

    /**
     * Create a new CredentialException with the specified credential.
     *
     * @param credential The credential that caused the exception.
     */
    public CredentialException(Object credential) {
        this.credential = credential;
    }

    /**
     * Create a new CredentialException with the specified credential and message.
     *
     * @param credential The credential that caused the exception.
     * @param message The detail message for the exception.
     */
    public CredentialException(Object credential, String message) {
        super(message);
        this.credential = credential;
    }

    /**
     * Create a new CredentialException with the specified credential, message, and cause.
     *
     * @param credential The credential that caused the exception.
     * @param message The detail message for the exception.
     * @param cause The cause of the exception (which is saved for later retrieval by the getCause() method).
     */
    public CredentialException(Object credential, String message, Throwable cause) {
        super(message, cause);
        this.credential = credential;
    }

    /**
     * Get the credential that caused the exception. This can be used to provide more context about the error, such as which username or password was invalid.
     *
     * @return The credential that caused the exception.
     */
    public Object getCredential() {
        return credential;
    }

    /**
     * Get additional details related to the credential exception. This can be used to provide more context about the error, such as which field was invalid or what the expected format was.
     *
     * @return A map of additional details related to the credential exception.
     */
    public Map<String, Object> getDetails() {
        return details;
    }

    /**
     * Get additional details related to the credential exception. This can be used to provide more context about the error, such as which field was invalid or what the expected format was.
     *
     * @param key The key for the detail.
     * @return The value associated with the key, or null if the key does not exist.
     */
    public Object get(String key) {
        return details.getOrDefault(key, null);
    }

    /**
     * Set additional details related to the credential exception. This can be used to provide more context about the error, such as which field was invalid or what the expected format was.
     *
     * @param key The key for the detail.
     * @param value The value for the detail.
     */
    public void set(String key, Object value) {
        details.put(key, value);
    }

    /**
     * Set additional details related to the credential exception. This can be used to provide more context about the error, such as which field was invalid or what the expected format was.
     *
     * @param key The key for the detail.
     * @param value The value for the detail.
     * @return The CredentialException instance, allowing for method chaining.
     */
    public CredentialException with(String key, Object value) {
        details.put(key, value);
        return this;
    }
}
