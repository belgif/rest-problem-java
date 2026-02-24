package io.github.belgif.rest.problem.spring.client;

import org.springframework.boot.web.client.RestClientCustomizer;

/**
 * RestClientCustomizer that registers the {@link ProblemResponseErrorHandler}.
 *
 * @see ProblemResponseErrorHandler
 */
public class ProblemRestClientCustomizer extends AbstractProblemRestClientCustomizer implements RestClientCustomizer {

    public ProblemRestClientCustomizer(ProblemResponseErrorHandler errorHandler) {
        super(errorHandler);
    }
}
