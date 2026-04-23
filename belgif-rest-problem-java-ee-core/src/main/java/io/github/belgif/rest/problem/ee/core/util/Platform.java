package io.github.belgif.rest.problem.ee.core.util;

/**
 * Utility class to detect the runtime platform.
 */
public class Platform {

    private static final boolean IS_QUARKUS = detectClass("io.quarkus.runtime.Quarkus");

    private Platform() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isQuarkus() {
        return IS_QUARKUS;
    }

    static boolean detectClass(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
