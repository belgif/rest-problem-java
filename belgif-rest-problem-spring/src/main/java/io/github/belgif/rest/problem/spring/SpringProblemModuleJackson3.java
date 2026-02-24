package io.github.belgif.rest.problem.spring;

import org.springframework.stereotype.Component;

import io.github.belgif.rest.problem.ProblemModuleJackson3;

/**
 * ProblemModule implementation for Spring Boot applications.
 *
 * @see SpringProblemTypeRegistry
 */
@Component
public class SpringProblemModuleJackson3 extends ProblemModuleJackson3 {

    public SpringProblemModuleJackson3(SpringProblemTypeRegistry springProblemTypeRegistry) {
        super(springProblemTypeRegistry);
    }

}
