package io.github.belgif.rest.problem.spring.boot.client;

import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.web.client.RestClient;

import io.github.belgif.rest.problem.spring.client.ProblemResponseErrorHandler;

/**
 * RestClientCustomizer that registers the {@link ProblemResponseErrorHandler}.
 *
 * @see ProblemResponseErrorHandler
 */
public class ProblemRestClientCustomizer implements RestClientCustomizer {

    private final ProblemResponseErrorHandler errorHandler;

    protected ProblemRestClientCustomizer(ProblemResponseErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void customize(RestClient.Builder restClientBuilder) {
        restClientBuilder.defaultStatusHandler(errorHandler);
    }

}
