package io.github.belgif.rest.problem.ee;

import javax.enterprise.inject.spi.CDI;

import io.github.belgif.rest.problem.ProblemModule;
import io.github.belgif.rest.problem.registry.ProblemTypeRegistry;

/**
 * ProblemModule implementation for Java EE applications.
 *
 * @see CdiProblemTypeRegistry
 */
public class CdiProblemModule extends ProblemModule {

    public CdiProblemModule() {
        super(CDI.current().select(ProblemTypeRegistry.class).get());
    }

}
