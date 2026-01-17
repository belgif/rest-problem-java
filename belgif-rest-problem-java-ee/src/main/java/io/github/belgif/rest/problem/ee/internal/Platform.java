package io.github.belgif.rest.problem.ee.internal;

public class Platform {

    private static final boolean QUARKUS = classPathContains("io.quarkus.runtime.Quarkus");

    private Platform() {
    }

    static boolean classPathContains(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isQuarkus() {
        return QUARKUS;
    }

}
