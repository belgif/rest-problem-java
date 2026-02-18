package io.github.belgif.rest.problem.spring;

import org.springframework.web.client.RestClient;

/**
 * RestClientCustomizer that registers the {@link AbstractProblemResponseErrorHandler}.
 *
 * @see AbstractProblemResponseErrorHandler
 */
public abstract class AbstractProblemRestClientCustomizer {

    private final AbstractProblemResponseErrorHandler errorHandler;

    protected AbstractProblemRestClientCustomizer(AbstractProblemResponseErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void customize(RestClient.Builder restClientBuilder) {
        restClientBuilder.defaultStatusHandler(errorHandler);
    }

}
