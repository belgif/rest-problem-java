package io.github.belgif.rest.problem.spring.client;

import org.springframework.web.client.ResponseErrorHandler;

/**
 * RestTemplate/RestClient error handler that converts problem responses to Problem exceptions.
 */
public interface ProblemResponseErrorHandler extends ResponseErrorHandler {
}
