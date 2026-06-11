package com.euonia.reflection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ClassScanner")
class ClassScannerTest {

    @Nested
    @DisplayName("scan")
    class Scan {

        @Test
        @DisplayName("Given existing package then return non-empty class list")
        void givenExistingPackageThenReturnNonEmptyList() {
            List<Class<?>> classes = ClassScanner.scan("com.euonia.reflection");

            assertNotNull(classes);
            assertFalse(classes.isEmpty(), "Should find at least ClassScanner itself");

            Set<String> classNames = classes.stream()
                    .map(Class::getSimpleName)
                    .collect(Collectors.toSet());

            assertTrue(classNames.contains("ClassScanner"),
                    "Should contain ClassScanner but got: " + classNames);
            assertTrue(classNames.contains("TypeHelper"),
                    "Should contain TypeHelper but got: " + classNames);
        }

        @Test
        @DisplayName("Given non-existent package then return empty list")
        void givenNonExistentPackageThenReturnEmptyList() {
            List<Class<?>> classes = ClassScanner.scan("com.euonia.nonexistent");

            assertNotNull(classes);
            assertTrue(classes.isEmpty(), "Should return empty list for non-existent package");
        }

        @Test
        @DisplayName("Given package then returned classes are not synthetic")
        void givenPackageThenReturnedClassesAreNotSynthetic() {
            List<Class<?>> classes = ClassScanner.scan("com.euonia.reflection");

            for (Class<?> cls : classes) {
                assertFalse(cls.isSynthetic(), cls.getName() + " should not be synthetic");
            }
        }

        @Test
        @DisplayName("Given package then returned classes are not anonymous")
        void givenPackageThenReturnedClassesAreNotAnonymous() {
            List<Class<?>> classes = ClassScanner.scan("com.euonia.reflection");

            for (Class<?> cls : classes) {
                assertFalse(cls.isAnonymousClass(), cls.getName() + " should not be anonymous");
            }
        }

        @Test
        @DisplayName("Given package with anonymous class then anonymous class is excluded")
        void givenPackageWithAnonymousClassThenAnonymousClassIsExcluded() {
            // Create an anonymous Runnable — its class will be in this package
            Runnable anonymous = new Runnable() {
                @Override
                public void run() {
                    // no-op
                }
            };
            Class<?> anonymousClass = anonymous.getClass();
            assertTrue(anonymousClass.isAnonymousClass(), "Precondition: class should be anonymous");

            List<Class<?>> classes = ClassScanner.scan("com.euonia.reflection");

            Set<String> classNames = classes.stream()
                    .map(Class::getName)
                    .collect(Collectors.toSet());

            assertFalse(classNames.contains(anonymousClass.getName()),
                    "Anonymous class should be filtered out: " + anonymousClass.getName());
        }

        @Test
        @DisplayName("Given null package name then throw")
        void givenNullPackageNameThenThrow() {
            assertThrows(NullPointerException.class, () -> ClassScanner.scan(null));
        }

        @Test
        @DisplayName("Given empty string package name then return empty list")
        void givenEmptyPackageNameThenReturnEmptyList() {
            List<Class<?>> classes = ClassScanner.scan("");

            assertNotNull(classes);
            assertTrue(classes.isEmpty(), "Default package scan should return empty");
        }

        @Test
        @DisplayName("Given valid package then no duplicate classes are returned")
        void givenValidPackageThenNoDuplicates() {
            List<Class<?>> classes = ClassScanner.scan("com.euonia.reflection");

            Set<Class<?>> uniqueClasses = new HashSet<>(classes);
            assertEquals(uniqueClasses.size(), classes.size(),
                    "Should not contain duplicate class entries");
        }
    }
}
