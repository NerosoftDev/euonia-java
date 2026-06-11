package com.euonia.reflection;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

/**
 * Scans the classpath for classes in a given package.
 * <p>
 * Supports both directory-based classpaths (typical in IDE / development)
 * and JAR-based classpaths (typical in packaged deployments).
 * Uses only standard Java APIs — no third-party dependencies.
 */
public final class ClassScanner {

    private ClassScanner() {
        // utility class
    }

    /**
     * Scans the specified package and returns all non-anonymous, non-synthetic
     * classes found within it.
     *
     * @param packageName the fully qualified package name (e.g. {@code com.euonia.bus})
     * @return a list of {@link Class} objects found in the package
     */
    public static List<Class<?>> scan(String packageName) {
        var classes = new ArrayList<Class<?>>();
        var path = packageName.replace('.', '/');
        var classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = ClassScanner.class.getClassLoader();
        }

        try {
            var resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                var resource = resources.nextElement();
                var protocol = resource.getProtocol();
                if ("file".equals(protocol)) {
                    scanDirectory(new File(URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8)),
                            packageName, classes);
                } else if ("jar".equals(protocol)) {
                    scanJar(resource, path, classes);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to scan package: " + packageName, e);
        }

        return classes;
    }

    /**
     * Recursively scans a directory for {@code .class} files.
     */
    private static void scanDirectory(File directory, String packageName, List<Class<?>> classes) {
        if (!directory.exists()) {
            return;
        }

        var files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (var file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), classes);
            } else if (file.getName().endsWith(".class")) {
                var className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                try {
                    var cls = Class.forName(className);
                    if (!cls.isSynthetic() && !cls.isAnonymousClass()) {
                        classes.add(cls);
                    }
                } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
                    // class cannot be loaded — skip it
                }
            }
        }
    }

    /**
     * Scans a JAR file for classes in the given package.
     */
    private static void scanJar(URL resource, String packagePath, List<Class<?>> classes) {
        var jarPath = resource.getPath();
        // Strip the "file:" prefix and "!/..." suffix to get the JAR file path
        var separatorIndex = jarPath.indexOf("!/");
        if (separatorIndex >= 0) {
            jarPath = jarPath.substring(0, separatorIndex);
        }
        // Remove leading "file:" if present
        if (jarPath.startsWith("file:")) {
            jarPath = jarPath.substring("file:".length());
        }
        jarPath = URLDecoder.decode(jarPath, StandardCharsets.UTF_8);

        try (var jar = new JarFile(jarPath)) {
            var entries = jar.entries();
            while (entries.hasMoreElements()) {
                var entry = entries.nextElement();
                var entryName = entry.getName();
                if (entryName.startsWith(packagePath + "/") && entryName.endsWith(".class")
                        && entryName.indexOf('/', packagePath.length() + 1) < 0) {
                    // Only direct children of the package (not sub-packages)
                    var className = entryName.substring(0, entryName.length() - 6).replace('/', '.');
                    try {
                        var cls = Class.forName(className);
                        if (!cls.isSynthetic() && !cls.isAnonymousClass()) {
                            classes.add(cls);
                        }
                    } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
                        // class cannot be loaded — skip it
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to scan JAR: " + jarPath, e);
        }
    }
}
