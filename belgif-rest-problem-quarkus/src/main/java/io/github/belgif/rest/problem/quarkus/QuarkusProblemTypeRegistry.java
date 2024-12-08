package io.github.belgif.rest.problem.quarkus;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.jsontype.NamedType;

import io.github.belgif.rest.problem.api.ProblemType;
import io.github.belgif.rest.problem.registry.ProblemTypeRegistry;

/**
 * ProblemTypeRegistry implementation for Quarkus.
 */
// NOTE: Can be moved to rest-problem-java-ee if we ever increase its baseline Jakarta EE 10
public class QuarkusProblemTypeRegistry implements ProblemTypeRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuarkusProblemTypeRegistry.class);

    private final NamedType[] problemTypes;

    public QuarkusProblemTypeRegistry(Class<?>[] problemTypes) {
        this.problemTypes = Arrays.stream(problemTypes).map(clazz -> {
            String type = clazz.getAnnotation(ProblemType.class).value();
            LOGGER.debug("Registered problem {}: {}", clazz, type);
            return new NamedType(clazz, type);
        }).toArray(NamedType[]::new);
    }

    @Override
    public NamedType[] getProblemTypes() {
        return problemTypes;
    }

}
