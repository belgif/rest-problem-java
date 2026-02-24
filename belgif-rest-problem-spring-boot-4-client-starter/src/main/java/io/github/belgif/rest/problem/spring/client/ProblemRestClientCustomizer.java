package io.github.belgif.rest.problem.spring.client;

import org.springframework.boot.restclient.RestClientCustomizer;

/**
 * RestClientCustomizer that registers the {@link ProblemResponseJackson3ErrorHandler}.
 *
 * @see ProblemResponseJackson3ErrorHandler
 */
public class ProblemRestClientCustomizer extends AbstractProblemRestClientCustomizer implements RestClientCustomizer {

    public ProblemRestClientCustomizer(AbstractProblemResponseErrorHandler errorHandler) {
        super(errorHandler);
    }

}
