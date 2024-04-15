package io.github.belgif.rest.problem.registry;

import com.fasterxml.jackson.databind.jsontype.NamedType;

import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.api.ProblemType;

/**
 * Dynamic registry of {@link Problem} classes annotated with @{@link ProblemType}.
 */
public interface ProblemTypeRegistry {

    NamedType[] getProblemTypes();

}
