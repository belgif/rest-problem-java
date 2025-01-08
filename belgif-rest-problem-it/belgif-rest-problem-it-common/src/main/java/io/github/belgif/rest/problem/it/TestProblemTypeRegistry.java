package io.github.belgif.rest.problem.it;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.jsontype.NamedType;

import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.api.ProblemType;
import io.github.belgif.rest.problem.registry.ProblemTypeRegistry;

public class TestProblemTypeRegistry implements ProblemTypeRegistry {

    private final List<NamedType> problemTypes = new ArrayList<>();

    @SafeVarargs
    public final void registerProblemType(Class<? extends Problem>... problem) {
        for (Class<? extends Problem> problemType : problem) {
            ProblemType annotation = problemType.getAnnotation(ProblemType.class);
            if (annotation != null) {
                problemTypes.add(new NamedType(problemType, annotation.value()));
            }
        }
    }

    @Override
    public NamedType[] getProblemTypes() {
        return problemTypes.toArray(new NamedType[0]);
    }

}
