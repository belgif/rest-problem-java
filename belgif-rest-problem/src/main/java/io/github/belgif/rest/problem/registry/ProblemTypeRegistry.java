package io.github.belgif.rest.problem.registry;

import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.api.ProblemType;
import tools.jackson.databind.jsontype.NamedType;

/**
 * Dynamic registry of {@link Problem} classes annotated with @{@link ProblemType}.
 */
public interface ProblemTypeRegistry {

    NamedType[] getProblemTypes();

}
