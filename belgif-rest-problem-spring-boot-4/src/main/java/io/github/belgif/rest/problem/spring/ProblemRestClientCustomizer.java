package io.github.belgif.rest.problem.spring;

import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * RestClientCustomizer that registers the {@link ProblemResponseErrorHandler}.
 *
 * @see ProblemResponseErrorHandler
 */
@Component
public class ProblemRestClientCustomizer implements RestClientCustomizer {

    private final ProblemResponseErrorHandler errorHandler;

    public ProblemRestClientCustomizer(ProblemResponseErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override
    public void customize(RestClient.Builder restClientBuilder) {
        restClientBuilder.defaultStatusHandler(errorHandler);
    }

}
