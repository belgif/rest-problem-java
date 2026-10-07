package io.github.belgif.rest.problem.spring.client;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.client.ResponseErrorHandler;

import io.github.belgif.rest.problem.api.Problem;

/**
 * RestTemplate/RestClient error handler that converts problem responses to Problem exceptions.
 */
public interface ProblemResponseErrorHandler extends ResponseErrorHandler {

    Problem handleProblem(int httpStatusCode, InputStream inputStream) throws IOException;

}
