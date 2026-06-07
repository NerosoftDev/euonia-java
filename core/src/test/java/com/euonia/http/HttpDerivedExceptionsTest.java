package com.euonia.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HTTP derived exceptions")
class HttpDerivedExceptionsTest {

    @ParameterizedTest(name = "{0} one-arg constructor sets status {1}")
    @MethodSource("exceptionFactories")
    void givenMessageWhenConstructingDerivedExceptionThenStatusAndMessageMatch(
        String name,
        int expectedStatus,
        Function<String, HttpStatusException> singleArgFactory,
        ExceptionBiFactory biFactory
    ) {
        HttpStatusException exception = singleArgFactory.apply("msg");

        assertEquals(expectedStatus, exception.getStatusCode());
        assertEquals("msg", exception.getMessage());
        assertNull(exception.getCause());
        assertTrue(exception.getClass().getSimpleName().contains(name));

        IllegalArgumentException cause = new IllegalArgumentException("cause");
        HttpStatusException withCause = biFactory.create("msg2", cause);

        assertEquals(expectedStatus, withCause.getStatusCode());
        assertEquals("msg2", withCause.getMessage());
        assertSame(cause, withCause.getCause());
    }

    private static Stream<Arguments> exceptionFactories() {
        return Stream.of(
            Arguments.of("BadRequest", 400, (Function<String, HttpStatusException>) BadRequestException::new, (ExceptionBiFactory) BadRequestException::new),
            Arguments.of("Forbidden", 403, (Function<String, HttpStatusException>) ForbiddenException::new, (ExceptionBiFactory) ForbiddenException::new),
            Arguments.of("ResourceNotFound", 404, (Function<String, HttpStatusException>) ResourceNotFoundException::new, (ExceptionBiFactory) ResourceNotFoundException::new),
            Arguments.of("MethodNotAllowed", 405, (Function<String, HttpStatusException>) MethodNotAllowedException::new, (ExceptionBiFactory) MethodNotAllowedException::new),
            Arguments.of("RequestTimeout", 408, (Function<String, HttpStatusException>) RequestTimeoutException::new, (ExceptionBiFactory) RequestTimeoutException::new),
            Arguments.of("Conflict", 409, (Function<String, HttpStatusException>) ConflictException::new, (ExceptionBiFactory) ConflictException::new),
            Arguments.of("UpgradeRequired", 426, (Function<String, HttpStatusException>) UpgradeRequiredException::new, (ExceptionBiFactory) UpgradeRequiredException::new),
            Arguments.of("TooManyRequests", 429, (Function<String, HttpStatusException>) TooManyRequestsException::new, (ExceptionBiFactory) TooManyRequestsException::new),
            Arguments.of("InternalServerError", 500, (Function<String, HttpStatusException>) InternalServerErrorException::new, (ExceptionBiFactory) InternalServerErrorException::new),
            Arguments.of("BadGateway", 502, (Function<String, HttpStatusException>) BadGatewayException::new, (ExceptionBiFactory) BadGatewayException::new),
            Arguments.of("ServiceUnavailable", 503, (Function<String, HttpStatusException>) ServiceUnavailableException::new, (ExceptionBiFactory) ServiceUnavailableException::new),
            Arguments.of("GatewayTimeout", 504, (Function<String, HttpStatusException>) GatewayTimeoutException::new, (ExceptionBiFactory) GatewayTimeoutException::new)
        );
    }

    @FunctionalInterface
    private interface ExceptionBiFactory {
        HttpStatusException create(String message, Throwable cause);
    }
}

