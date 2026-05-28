package com.euonia.security;

import javax.security.auth.Subject;

public class UserPrincipal {
    private final Subject subject;

    public UserPrincipal(Subject subject) {
        this.subject = subject;
    }

    public Subject getSubject() {
        return subject;
    }

    public String getName() {
        return subject.getPrincipals().stream()
                .filter(principal -> principal instanceof java.security.Principal)
                .map(principal -> ((java.security.Principal) principal).getName())
                .findFirst()
                .orElse(null);
    }

    public String getClaim(String claimType) {
        // return subject.getPrincipals().stream()
        //         .filter(principal -> principal instanceof UserClaim)
        //         .map(principal -> (UserClaim) principal)
        //         .filter(claim -> claim.getType().equals(claimType))
        //         .map(UserClaim::getValue)
        //         .findFirst()
        //         .orElse(null);

        return null; // Placeholder until UserClaim implementation is added
    }

    public boolean isAuthenticated() {
        return subject != null && !subject.getPrincipals().isEmpty();
    }

    public boolean hasRole(String role) {
        return subject != null && subject.getPrincipals().stream()
                .anyMatch(principal -> principal instanceof java.security.Principal &&
                        ((java.security.Principal) principal).getName().equals(role));
    }

    public boolean isInRoles(String... roles) {
        return subject != null && subject.getPrincipals().stream()
                .anyMatch(principal -> principal instanceof java.security.Principal &&
                        java.util.Arrays.asList(roles).contains(((java.security.Principal) principal).getName()));
    }

    public void ensureAuthenticated() {
        if (!isAuthenticated()) {
            throw new AuthenticationException("User is not authenticated");
        }
    }

    public void ensureHasRole(String role) {
        ensureAuthenticated();

        if (subject.getPrincipals().stream()
                .noneMatch(principal -> principal instanceof java.security.Principal &&
                        ((java.security.Principal) principal).getName().equals(role))) {
            throw new UnauthorizedAccessException("User does not have required role: " + role);
        }
    }

    public void ensureInRoles(String... roles) {
        ensureAuthenticated();
        if (subject.getPrincipals().stream()
                .noneMatch(principal -> principal instanceof java.security.Principal &&
                        java.util.Arrays.asList(roles).contains(((java.security.Principal) principal).getName()))) {
            throw new UnauthorizedAccessException("User does not have any of the required roles: " + String.join(", ", roles));
        }
    }

    // public void ensureHasClaim(String claimType) {
    //     if (subject == null || subject.getPrincipals().stream()
    //             .noneMatch(principal -> principal instanceof UserClaim &&
    //                     ((UserClaim) principal).getType().equals(claimType))) {
    //         throw new UnauthorizedAccessException("User does not have required claim: " + claimType);
    //     }
    // }
}
