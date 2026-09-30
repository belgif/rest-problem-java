package io.github.belgif.rest.problem.spring.boot.client;

import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
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
        restClientBuilder.requestFactory(
                new BufferingClientHttpRequestFactory(ClientHttpRequestFactoryBuilder.detect().build()));
    }
}
