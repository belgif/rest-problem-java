package io.github.belgif.rest.problem.spring.client;

import org.springframework.web.client.RestClient;

/**
 * RestClientCustomizer that registers the {@link ProblemResponseErrorHandler}.
 *
 * @see ProblemResponseErrorHandler
 */
public abstract class AbstractProblemRestClientCustomizer {

    private final ProblemResponseErrorHandler errorHandler;

    protected AbstractProblemRestClientCustomizer(ProblemResponseErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void customize(RestClient.Builder restClientBuilder) {
        restClientBuilder.defaultStatusHandler(errorHandler);
    }

}
