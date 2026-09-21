package io.github.belgif.rest.problem.spring.client;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.spring.ProblemMediaType;

public abstract class AbstractProblemResponseErrorHandler extends DefaultResponseErrorHandler
        implements ProblemResponseErrorHandler {

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        if (ProblemMediaType.INSTANCE.isCompatibleWith(response.getHeaders().getContentType())
                || response.getStatusCode().isError()
                        && MediaType.APPLICATION_JSON.isCompatibleWith(response.getHeaders().getContentType())) {
            Problem problem = handleProblem(response.getStatusCode().value(), response.getBody());
            if (problem == null) {
                return;
            }
            throw problem;
        }
        super.handleError(url, method, response);
    }

}
