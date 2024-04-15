package io.github.belgif.rest.problem;

import io.github.belgif.rest.problem.registry.CdiProblemTypeRegistry;

/**
 * ProblemModule implementation for Java EE applications.
 *
 * @see CdiProblemTypeRegistry
 */
public class CdiProblemModule extends ProblemModule {

    public CdiProblemModule() {
        super(CdiProblemTypeRegistry.instance());
    }

}
