package io.github.belgif.rest.problem.it;

import java.util.HashMap;
import java.util.Map;

import io.github.belgif.rest.problem.api.ProblemType;
import io.github.belgif.rest.problem.registry.ProblemTypeRegistry;

public class TestProblemTypeRegistry implements ProblemTypeRegistry {

    private final Map<String, Class<?>> problemTypes = new HashMap<>();

    @SafeVarargs
    public final void registerProblemType(Class<?>... problem) {
        for (Class<?> problemType : problem) {
            ProblemType annotation = problemType.getAnnotation(ProblemType.class);
            if (annotation != null) {
                problemTypes.put(annotation.value(), problemType);
            }
        }
    }

    @Override
    public Map<String, Class<?>> getProblemTypes() {
        return problemTypes;
    }

}
