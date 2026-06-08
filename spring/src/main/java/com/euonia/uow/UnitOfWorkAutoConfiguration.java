package com.euonia.uow;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Spring auto-configuration for the Unit of Work module.
 *
 * <p>Registers the following beans:
 * <ul>
 *   <li>{@link UnitOfWorkAccessor} — thread-local holder for the
 *       ambient unit of work</li>
 *   <li>{@link UnitOfWorkManager} — entry point for creating and
 *       managing units of work</li>
 *   <li>{@link UnitOfWorkAspect} — AOP aspect that wraps
 *       {@code @UnitOfWork}-annotated methods</li>
 * </ul>
 *
 * <p>Enable AspectJ auto-proxy so the {@link UnitOfWorkAspect} can
 * intercept annotated Spring beans.</p>
 *
 * <h3>Usage</h3>
 * <p>For Spring Boot, this configuration is auto-detected via
 * {@code spring.factories}. For plain Spring, import it manually:</p>
 * <pre>{@code
 * @Configuration
 * @Import(UnitOfWorkAutoConfiguration.class)
 * public class AppConfig { }
 * }</pre>
 *
 * @see UnitOfWorkAspect
 * @see UnitOfWorkManager
 */
@Configuration
@EnableAspectJAutoProxy
public class UnitOfWorkAutoConfiguration {

    /**
     * Creates the thread-local accessor for tracking the ambient
     * unit of work.
     *
     * @return a new {@link UnitOfWorkAccessor}
     */
    @Bean
    public UnitOfWorkAccessor unitOfWorkAccessor() {
        return new UnitOfWorkAccessor();
    }

    /**
     * Creates the unit-of-work manager.
     *
     * @param accessor the thread-local accessor
     * @return a new {@link UnitOfWorkManager}
     */
    @Bean
    public UnitOfWorkManager unitOfWorkManager(UnitOfWorkAccessor accessor) {
        return new UnitOfWorkManager(accessor, new UnitOfWorkOptions());
    }

    /**
     * Creates the AOP aspect that wraps {@code @UnitOfWork}-annotated
     * methods in a unit of work.
     *
     * @param manager the unit-of-work manager
     * @return a new {@link UnitOfWorkAspect}
     */
    @Bean
    public UnitOfWorkAspect unitOfWorkAspect(UnitOfWorkManager manager) {
        return new UnitOfWorkAspect(manager);
    }
}
