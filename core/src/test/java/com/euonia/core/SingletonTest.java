package com.euonia.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Singleton")
class SingletonTest {

    @Test
    @DisplayName("Given class with default constructor when getting instance twice then same instance is returned")
    void givenClassWithDefaultConstructorWhenGettingTwiceThenReturnSameInstance() {
        SampleService first = Singleton.getInstance(SampleService.class);
        SampleService second = Singleton.getInstance(SampleService.class);

        assertSame(first, second);
    }

    @Test
    @DisplayName("Given class and supplier when getting twice then supplier runs once and same instance is returned")
    void givenClassAndSupplierWhenGettingTwiceThenSupplierRunsOnce() {
        AtomicInteger counter = new AtomicInteger();

        ProvidedService first = Singleton.get(ProvidedService.class, () -> {
            counter.incrementAndGet();
            return new ProvidedService("v1");
        });
        ProvidedService second = Singleton.get(ProvidedService.class, () -> {
            counter.incrementAndGet();
            return new ProvidedService("v2");
        });

        assertSame(first, second);
        assertEquals("v1", first.value);
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("Given class without default constructor when getting instance then runtime exception is thrown")
    void givenClassWithoutDefaultConstructorWhenGettingInstanceThenThrowRuntimeException() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> Singleton.getInstance(NoDefaultConstructor.class));

        assertTrue(exception.getMessage().contains(NoDefaultConstructor.class.getName()));
    }

    static class SampleService {
        SampleService() {
        }
    }

    static class ProvidedService {
        private final String value;

        ProvidedService(String value) {
            this.value = value;
        }
    }

    static class NoDefaultConstructor {
        NoDefaultConstructor(String value) {
        }
    }
}

