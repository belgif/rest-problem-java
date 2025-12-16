package io.github.belgif.rest.problem.quarkus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.belgif.rest.problem.api.ProblemType;
import io.github.belgif.rest.problem.registry.ProblemTypeRegistry;

/**
 * ProblemTypeRegistry implementation for Quarkus.
 */
// NOTE: Can be moved to rest-problem-java-ee if we ever increase its baseline Jakarta EE 10
public class QuarkusProblemTypeRegistry implements ProblemTypeRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuarkusProblemTypeRegistry.class);

    private final Map<String, Class<?>> problemTypes = new HashMap<>();

    public QuarkusProblemTypeRegistry(Class<?>[] problemTypes) {
        for (Class<?> problemType : problemTypes) {
            String type = problemType.getAnnotation(ProblemType.class).value();
            LOGGER.debug("Registered problem {}: {}", problemType, type);
            this.problemTypes.put(type, problemType);
        }
    }

    @Override
    public Map<String, Class<?>> getProblemTypes() {
        return Collections.unmodifiableMap(problemTypes);
    }

}
