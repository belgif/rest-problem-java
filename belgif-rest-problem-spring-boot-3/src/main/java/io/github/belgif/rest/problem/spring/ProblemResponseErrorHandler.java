package io.github.belgif.rest.problem.spring;

import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;

import io.github.belgif.rest.problem.DefaultProblem;
import io.github.belgif.rest.problem.api.Problem;
import tools.jackson.databind.ObjectMapper;

/**
 * RestTemplate/RestClient error handler that converts problem responses to Problem exceptions.
 */
@Component
public class ProblemResponseErrorHandler extends DefaultResponseErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProblemResponseErrorHandler.class);

    private final ObjectMapper objectMapper;

    public ProblemResponseErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        if (ProblemMediaType.INSTANCE.isCompatibleWith(response.getHeaders().getContentType())
                || response.getStatusCode().isError()
                        && MediaType.APPLICATION_JSON.isCompatibleWith(response.getHeaders().getContentType())) {
            Problem problem = objectMapper.readValue(response.getBody(), Problem.class);
            if (problem instanceof DefaultProblem) {
                LOGGER.info("No @ProblemType registered for {}: using DefaultProblem fallback", problem.getType());
            }
            throw problem;
        }
        super.handleError(url, method, response);
    }
}
