package com.euonia.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.security.auth.Subject;
import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserPrincipal")
class UserPrincipalTest {

    @Test
    @DisplayName("Given subject with principals when querying principal info then name authentication and roles are resolved")
    void givenSubjectWithPrincipalsWhenQueryingThenNameAuthAndRolesResolve() {
        Subject subject = new Subject();
        subject.getPrincipals().add((Principal) () -> "alice");
        subject.getPrincipals().add((Principal) () -> "admin");

        UserPrincipal userPrincipal = new UserPrincipal(subject);

        assertSame(subject, userPrincipal.getSubject());
        assertEquals("alice", userPrincipal.getName());
        assertTrue(userPrincipal.isAuthenticated());
        assertTrue(userPrincipal.hasRole("admin"));
        assertTrue(userPrincipal.isInRoles("guest", "admin"));
        assertNull(userPrincipal.getClaim(UserClaimTypes.EMAIL));
    }

    @Test
    @DisplayName("Given null or empty subject when querying then unauthenticated behavior is returned")
    void givenNullOrEmptySubjectWhenQueryingThenUnauthenticatedBehaviorReturned() {
        UserPrincipal withNullSubject = new UserPrincipal(null);
        UserPrincipal withEmptySubject = new UserPrincipal(new Subject());

        assertFalse(withNullSubject.isAuthenticated());
        assertFalse(withNullSubject.hasRole("admin"));
        assertFalse(withNullSubject.isInRoles("admin"));
        assertThrows(NullPointerException.class, withNullSubject::getName);

        assertFalse(withEmptySubject.isAuthenticated());
        assertFalse(withEmptySubject.hasRole("admin"));
        assertFalse(withEmptySubject.isInRoles("admin"));
        assertNull(withEmptySubject.getName());
    }

    @Test
    @DisplayName("Given unauthenticated or unauthorized user when ensuring access then security exceptions are thrown")
    void givenUnauthenticatedOrUnauthorizedUserWhenEnsuringAccessThenThrow() {
        UserPrincipal unauthenticated = new UserPrincipal(new Subject());

        AuthenticationException authException = assertThrows(AuthenticationException.class, unauthenticated::ensureAuthenticated);
        assertEquals("User is not authenticated", authException.getMessage());

        Subject subject = new Subject();
        subject.getPrincipals().add((Principal) () -> "user");
        UserPrincipal authenticated = new UserPrincipal(subject);

        UnauthorizedAccessException roleException = assertThrows(UnauthorizedAccessException.class,
            () -> authenticated.ensureHasRole("admin"));
        assertEquals("User does not have required role: admin", roleException.getMessage());

        UnauthorizedAccessException rolesException = assertThrows(UnauthorizedAccessException.class,
            () -> authenticated.ensureInRoles("admin", "ops"));
        assertEquals("User does not have any of the required roles: admin, ops", rolesException.getMessage());
    }

    @Test
    @DisplayName("Given authorized user when ensuring role membership then no exception is thrown")
    void givenAuthorizedUserWhenEnsuringRoleMembershipThenNoExceptionThrown() {
        Subject subject = new Subject();
        subject.getPrincipals().add((Principal) () -> "dev");
        UserPrincipal userPrincipal = new UserPrincipal(subject);

        assertDoesNotThrow(userPrincipal::ensureAuthenticated);
        assertDoesNotThrow(() -> userPrincipal.ensureHasRole("dev"));
        assertDoesNotThrow(() -> userPrincipal.ensureInRoles("ops", "dev"));
    }
}

