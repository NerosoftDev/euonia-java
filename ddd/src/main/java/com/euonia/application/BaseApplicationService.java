package com.euonia.application;

import com.euonia.reflection.ServiceProvider;
import com.euonia.security.UserPrincipal;

/**
 * BaseApplicationService is an abstract class that provides common functionality for application services.
 * It implements the ApplicationService interface and uses a ServiceProvider to access other services, such as UserPrincipal, which represents the currently authenticated user.
 * This class can be extended by concrete application service implementations to inherit the ability to resolve services and access user information without needing to implement these features in each service.
 */
public abstract class BaseApplicationService implements ApplicationService {

    protected final ServiceProvider serviceProvider;

    protected BaseApplicationService(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    protected <T> T getService(Class<T> type) {
        return (T) serviceProvider.getService(type);
    }

    protected UserPrincipal getUser() {
        return serviceProvider.getService(UserPrincipal.class);
    }
}
