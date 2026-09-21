package io.github.belgif.rest.problem.spring.client;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.github.belgif.rest.problem.DefaultProblem;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.internal.Jackson3Util;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * RestTemplate/RestClient error handler that converts problem responses to Problem exceptions.
 */
@Component
public class ProblemResponseJackson3ErrorHandler extends AbstractProblemResponseErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProblemResponseJackson3ErrorHandler.class);

    private final ObjectMapper objectMapper;

    public ProblemResponseJackson3ErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Problem handleProblem(int httpStatusCode, InputStream inputStream) {
        JsonNode json = objectMapper.readTree(inputStream);
        if (httpStatusCode == 503 && Jackson3Util.isHealthDownResponse(json)) {
            return null;
        }
        Problem problem = objectMapper.convertValue(json, Problem.class);
        if (problem instanceof DefaultProblem) {
            LOGGER.info("No @ProblemType registered for {}: using DefaultProblem fallback", problem.getType());
        }
        Jackson3Util.checkStatusCodeConsistency(httpStatusCode, json, problem);
        return problem;
    }

}
