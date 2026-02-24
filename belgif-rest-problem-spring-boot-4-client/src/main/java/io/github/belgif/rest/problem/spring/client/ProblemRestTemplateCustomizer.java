package io.github.belgif.rest.problem.spring.client;

import org.springframework.boot.restclient.RestTemplateCustomizer;

/**
 * RestTemplateCustomizer that registers the {@link ProblemResponseJackson3ErrorHandler}.
 *
 * @see ProblemResponseJackson3ErrorHandler
 */
public class ProblemRestTemplateCustomizer extends AbstractProblemRestTemplateCustomizer
        implements RestTemplateCustomizer {

    public ProblemRestTemplateCustomizer(AbstractProblemResponseErrorHandler problemResponseErrorHandler) {
        super(problemResponseErrorHandler);
    }
}
