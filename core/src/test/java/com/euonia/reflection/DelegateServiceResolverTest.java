package com.euonia.reflection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DelegateServiceResolver")
class DelegateServiceResolverTest {

    @Test
    @DisplayName("Given registered service when resolving then delegated instance is returned")
    void givenRegisteredServiceWhenResolvingThenReturnDelegatedInstance() {
        SampleService instance = new SampleService();
        DelegateServiceResolver resolver = new DelegateServiceResolver(type -> type == SampleService.class ? instance : null);

        assertSame(instance, resolver.getService(SampleService.class));
        assertNull(resolver.getService(NeedsArguments.class));
    }

    @Test
    @DisplayName("Given matching constructor arguments when creating instance then object is constructed")
    void givenMatchingConstructorArgumentsWhenCreatingInstanceThenReturnConstructedObject() {
        DelegateServiceResolver resolver = new DelegateServiceResolver(type -> null);

        NeedsArguments instance = resolver.createInstance(NeedsArguments.class, "demo", 3);

        assertEquals("demo", instance.name);
        assertEquals(3, instance.count);
    }

    @Test
    @DisplayName("Given no matching constructor when creating instance then illegal state is thrown")
    void givenNoMatchingConstructorWhenCreatingInstanceThenThrowIllegalState() {
        DelegateServiceResolver resolver = new DelegateServiceResolver(type -> null);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> resolver.createInstance(NeedsArguments.class, 42));

        assertEquals("Could not construct type: " + NeedsArguments.class.getName(), exception.getMessage());
    }

    static class SampleService {
        SampleService() {
        }
    }

    static class NeedsArguments {
        private final String name;
        private final int count;

        NeedsArguments(String name, int count) {
            this.name = name;
            this.count = count;
        }
    }
}
