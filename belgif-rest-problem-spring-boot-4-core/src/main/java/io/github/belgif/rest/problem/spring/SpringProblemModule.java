package io.github.belgif.rest.problem.spring;

import org.springframework.stereotype.Component;

import io.github.belgif.rest.problem.ProblemModuleJackson3;

/**
 * ProblemModule implementation for Spring Boot applications.
 *
 * @see SpringProblemTypeRegistry
 */
@Component
public class SpringProblemModule extends ProblemModuleJackson3 {

    public SpringProblemModule(SpringProblemTypeRegistry springProblemTypeRegistry) {
        super(springProblemTypeRegistry);
    }

}
