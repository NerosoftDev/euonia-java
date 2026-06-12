package com.euonia.reflection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DelegateServiceProvider")
class DelegateServiceProviderTest {

    @Test
    @DisplayName("Given registered service when resolving then delegated instance is returned")
    void givenRegisteredServiceWhenResolvingThenReturnDelegatedInstance() {
        SampleService instance = new SampleService();
        DelegateServiceProvider resolver = new DelegateServiceProvider(type -> type == SampleService.class ? instance : null);

        assertSame(instance, resolver.getService(SampleService.class).orElse(null));
        assertTrue(resolver.getService(NeedsArguments.class).isEmpty());
    }

    @Test
    @DisplayName("Given matching constructor arguments when creating instance then object is constructed")
    void givenMatchingConstructorArgumentsWhenCreatingInstanceThenReturnConstructedObject() {
        DelegateServiceProvider resolver = new DelegateServiceProvider(type -> null);

        NeedsArguments instance = resolver.createInstance(NeedsArguments.class, "demo", 3);

        assertEquals("demo", instance.name);
        assertEquals(3, instance.count);
    }

    @Test
    @DisplayName("Given no matching constructor when creating instance then illegal state is thrown")
    void givenNoMatchingConstructorWhenCreatingInstanceThenThrowIllegalState() {
        DelegateServiceProvider resolver = new DelegateServiceProvider(type -> null);

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
