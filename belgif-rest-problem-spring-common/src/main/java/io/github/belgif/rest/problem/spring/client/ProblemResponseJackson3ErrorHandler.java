package io.github.belgif.rest.problem.spring.client;

import org.springframework.stereotype.Component;

import io.github.belgif.rest.problem.api.Problem;
import tools.jackson.databind.ObjectMapper;

/**
 * RestTemplate/RestClient error handler that converts problem responses to Problem exceptions.
 */
@Component
public class ProblemResponseJackson3ErrorHandler extends AbstractProblemResponseErrorHandler {

    public ProblemResponseJackson3ErrorHandler(ObjectMapper objectMapper) {
        super(input -> objectMapper.readValue(input, Problem.class));
    }

}
