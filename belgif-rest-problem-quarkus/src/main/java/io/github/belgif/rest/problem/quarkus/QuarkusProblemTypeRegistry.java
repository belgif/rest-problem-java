package io.github.belgif.rest.problem.quarkus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    private final Map<String, Class<?>> problemTypes;

    public QuarkusProblemTypeRegistry(Class<?>[] problemTypes) {
        this.problemTypes = Arrays.stream(problemTypes).collect(
                Collectors.toMap(
                        clazz -> {
                            String type = clazz.getAnnotation(ProblemType.class).value();
                            LOGGER.debug("Registered problem {}: {}", clazz, type);
                            return type;
                        },
                        clazz -> clazz,
                        (oldVal, newVal) -> oldVal,
                        HashMap::new));
    }

    @Override
    public Map<String, Class<?>> getProblemTypes() {
        return problemTypes;
    }

}
