package io.github.belgif.rest.problem.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * RestClientCustomizer that registers the {@link ProblemResponseErrorHandler}.
 *
 * @see ProblemResponseErrorHandler
 */
@Component
@ConditionalOnClass({ RestClient.class, RestClientCustomizer.class })
public class ProblemRestClientCustomizer extends AbstractProblemRestClientCustomizer implements RestClientCustomizer {

    public ProblemRestClientCustomizer(ProblemResponseErrorHandler errorHandler) {
        super(errorHandler);
    }
}
