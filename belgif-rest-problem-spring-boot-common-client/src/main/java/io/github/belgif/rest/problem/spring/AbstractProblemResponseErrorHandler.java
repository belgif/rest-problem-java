package io.github.belgif.rest.problem.spring;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import io.github.belgif.rest.problem.DefaultProblem;
import io.github.belgif.rest.problem.api.Problem;

/**
 * RestTemplate/RestClient error handler that converts problem responses to Problem exceptions.
 */
public abstract class AbstractProblemResponseErrorHandler extends DefaultResponseErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProblemResponseErrorHandler.class);

    private final ProblemReader problemReader;

    protected AbstractProblemResponseErrorHandler(ProblemReader problemReader) {
        this.problemReader = problemReader;
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        if (ProblemMediaType.INSTANCE.isCompatibleWith(response.getHeaders().getContentType())
                || response.getStatusCode().isError()
                        && MediaType.APPLICATION_JSON.isCompatibleWith(response.getHeaders().getContentType())) {
            Problem problem = problemReader.read(response.getBody());
            if (problem instanceof DefaultProblem) {
                LOGGER.info("No @ProblemType registered for {}: using DefaultProblem fallback", problem.getType());
            }
            throw problem;
        }
        super.handleError(url, method, response);
    }

    @FunctionalInterface
    public interface ProblemReader {
        Problem read(InputStream body) throws IOException;
    }

}
