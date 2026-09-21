package io.github.belgif.rest.problem.spring.client;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.belgif.rest.problem.DefaultProblem;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.internal.Jackson2Util;

/**
 * RestTemplate/RestClient error handler that converts problem responses to Problem exceptions.
 */
@Component
public class ProblemResponseJackson2ErrorHandler extends AbstractProblemResponseErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProblemResponseJackson2ErrorHandler.class);

    private final ObjectMapper objectMapper;

    public ProblemResponseJackson2ErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Problem handleProblem(int httpStatusCode, InputStream inputStream) throws IOException {
        JsonNode json = objectMapper.readTree(inputStream);
        if (httpStatusCode == 503 && Jackson2Util.isHealthDownResponse(json)) {
            return null;
        }
        Problem problem = objectMapper.convertValue(json, Problem.class);
        if (problem instanceof DefaultProblem) {
            LOGGER.info("No @ProblemType registered for {}: using DefaultProblem fallback", problem.getType());
        }
        Jackson2Util.checkStatusCodeConsistency(httpStatusCode, json, problem);
        return problem;
    }

}
