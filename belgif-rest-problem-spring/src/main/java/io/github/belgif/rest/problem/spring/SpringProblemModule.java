package io.github.belgif.rest.problem.spring;

import org.springframework.stereotype.Component;

import io.github.belgif.rest.problem.ProblemModule;

/**
 * ProblemModule implementation for Spring Boot applications.
 *
 * @see SpringProblemTypeRegistry
 */
@Component
public class SpringProblemModule extends ProblemModule {

    public SpringProblemModule(SpringProblemTypeRegistry springProblemTypeRegistry) {
        super(springProblemTypeRegistry);
    }

}
