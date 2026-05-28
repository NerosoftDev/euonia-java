package com.euonia.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseHttpStatusCode {
    /**
     * The HTTP status code associated with the response.
     * This value is used to indicate the status of the HTTP response, such as 400 for bad request, 404 for not found, etc.
     *
     * @return The HTTP status code for the response.
     */
    int value();
}
