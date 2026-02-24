package io.github.belgif.rest.problem.spring.client;

import org.springframework.boot.web.client.RestClientCustomizer;

/**
 * RestClientCustomizer that registers the {@link ProblemResponseJackson2ErrorHandler}.
 *
 * @see ProblemResponseJackson2ErrorHandler
 */
public class ProblemRestClientCustomizer extends AbstractProblemRestClientCustomizer implements RestClientCustomizer {

    public ProblemRestClientCustomizer(AbstractProblemResponseErrorHandler errorHandler) {
        super(errorHandler);
    }
}
