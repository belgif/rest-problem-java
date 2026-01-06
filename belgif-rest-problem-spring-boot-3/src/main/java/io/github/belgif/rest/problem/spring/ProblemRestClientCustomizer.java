package io.github.belgif.rest.problem.spring;

import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.stereotype.Component;

/**
 * RestClientCustomizer that registers the {@link ProblemResponseErrorHandler}.
 *
 * @see ProblemResponseErrorHandler
 */
@Component
public class ProblemRestClientCustomizer extends AbstractProblemRestClientCustomizer implements RestClientCustomizer {

    public ProblemRestClientCustomizer(ProblemResponseErrorHandler errorHandler) {
        super(errorHandler);
    }
}
