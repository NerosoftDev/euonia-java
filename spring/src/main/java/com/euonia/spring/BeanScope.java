package com.euonia.spring;

/**
 * Defines the scopes of a bean in the Spring IoC container.
 * <p>>
 * The scopes are defined as constants in this class, and can be used to specify the scope of a bean when defining it in the Spring configuration.
 * <p>>
 * The scopes defined in this class are:
 * <ul>
 *     <li>{@link #APPLICATION}: Scopes a single bean definition to the lifecycle of a ServletContext. Only valid in the context of a web-aware Spring ApplicationContext.</li>
 *     <li>{@link #PROTOTYPE}: Scopes a single bean definition to any number of object instances.</li>
 *     <li>{@link #REQUEST}: Scopes a single bean definition to the lifecycle of a single HTTP request. Only valid in the context of a web-aware Spring ApplicationContext.</li>
 *     <li>{@link #SESSION}: Scopes a single bean definition to the lifecycle of an HTTP Session. Only valid in the context of a web-aware Spring ApplicationContext.</li>
 *     <li>{@link #SINGLETON}: Scopes a single bean definition to a single object instance for each Spring IoC container.</li>
 *     <li>{@link #WEB_SOCKET}: Scopes a single bean definition to the lifecycle of a WebSocket. Only valid in the context of a web-aware Spring ApplicationContext.</li>
 * </ul>
 */
public class BeanScope {
    /**
     * Scopes a single bean definition to the lifecycle of a ServletContext.
     * Only valid in the context of a web-aware Spring ApplicationContext.
     */
    public final static String APPLICATION = "application";

    /**
     * Scopes a single bean definition to any number of object instances.
     */
    public final static String PROTOTYPE = "prototype";

    /**
     * Scopes a single bean definition to the lifecycle of a single HTTP request.
     * That is, each HTTP request has its own instance of a bean created off the back of a single bean definition.
     * Only valid in the context of a web-aware Spring ApplicationContext.
     */
    public final static String REQUEST = "request";

    /**
     * Scopes a single bean definition to the lifecycle of an HTTP Session.
     * Only valid in the context of a web-aware Spring ApplicationContext.
     */
    public final static String SESSION = "session";

    /**
     * Scopes a single bean definition to a single object instance for each Spring IoC container.
     */
    public final static String SINGLETON = "singleton";

    /**
     * Scopes a single bean definition to the lifecycle of a WebSocket.
     * Only valid in the context of a web-aware Spring ApplicationContext.
     */
    public final static String WEB_SOCKET = "websocket";

    /*
    APPLICATION("application"),
    PROTOTYPE("prototype"),
    REQUEST("request"),
    SESSION("session"),
    SINGLETON("singleton"),
    WEB_SOCKET("websocket");

    private final String value;

    BeanScope(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    */
}
