package com.euonia.application;

import com.euonia.reflection.ServiceResolver;
import com.euonia.security.UserPrincipal;

/**
 * BaseApplicationService is an abstract class that provides common functionality for application services.
 * It implements the ApplicationService interface and uses a ServiceResolver to access other services, such as UserPrincipal, which represents the currently authenticated user.
 * This class can be extended by concrete application service implementations to inherit the ability to resolve services and access user information without needing to implement these features in each service.
 */
public abstract class BaseApplicationService implements ApplicationService {

    protected final ServiceResolver serviceResolver;

    protected BaseApplicationService(ServiceResolver serviceResolver) {
        this.serviceResolver = serviceResolver;
    }

    protected <T> T getService(Class<T> type) {
        return (T) serviceResolver.getService(type);
    }

    protected UserPrincipal getUser() {
        return serviceResolver.getService(UserPrincipal.class);
    }
}
