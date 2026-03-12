package io.github.belgif.rest.problem.spring.client;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.belgif.rest.problem.api.Problem;

/**
 * RestTemplate/RestClient error handler that converts problem responses to Problem exceptions.
 */
@Component
public class ProblemResponseJackson2ErrorHandler extends AbstractProblemResponseErrorHandler {

    public ProblemResponseJackson2ErrorHandler(ObjectMapper objectMapper) {
        super(input -> objectMapper.readValue(input, Problem.class));
    }

}
