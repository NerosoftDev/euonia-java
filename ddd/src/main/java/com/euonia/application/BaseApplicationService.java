package com.euonia.application;

import com.euonia.bus.Bus;
import com.euonia.reflection.ServiceProvider;
import com.euonia.security.UserPrincipal;

import java.util.Optional;

/**
 * BaseApplicationService is an abstract class that provides common functionality for application services.
 * It implements the ApplicationService interface and uses a ServiceProvider to access other services, such as UserPrincipal, which represents the currently authenticated user.
 * This class can be extended by concrete application service implementations to inherit the ability to resolve services and access user information without needing to implement these features in each service.
 */
public abstract class BaseApplicationService implements ApplicationService {

    protected final ServiceProvider provider;
    protected final Bus bus;

    protected BaseApplicationService(ServiceProvider provider) {
        this.provider = provider;
        this.bus = provider.getService(Bus.class).orElse(null);
    }

    /**
     * Resolves a service of the specified type from the ServiceProvider.
     *
     * @param type The class type of the service to resolve.
     * @param <T>  The type of the service to resolve.
     * @return An Optional containing the resolved service, or an empty Optional if the service is not available.
     */
    protected <T> Optional<T> getService(Class<T> type) {
        return provider.getService(type);
    }

    /**
     * Gets the currently authenticated user from the ServiceProvider.
     *
     * @return The currently authenticated user, or null if not available.
     */
    protected UserPrincipal getUser() {
        return getService(UserPrincipal.class).orElse(null);
    }

    /**
     * Gets the Bus service from the ServiceProvider.
     *
     * @return The Bus service, or null if not available.
     */
    protected Bus getBus() {
        return bus;
    }
}
