package io.github.belgif.rest.problem.spring.client;

import org.springframework.boot.web.client.RestTemplateCustomizer;

/**
 * RestTemplateCustomizer that registers the {@link ProblemResponseJackson2ErrorHandler}.
 *
 * @see ProblemResponseJackson2ErrorHandler
 */
public class ProblemRestTemplateCustomizer extends AbstractProblemRestTemplateCustomizer
        implements RestTemplateCustomizer {

    public ProblemRestTemplateCustomizer(AbstractProblemResponseErrorHandler problemResponseErrorHandler) {
        super(problemResponseErrorHandler);
    }

}
