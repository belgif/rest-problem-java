package io.github.belgif.rest.problem.spring.client;

import org.springframework.boot.web.client.RestTemplateCustomizer;

/**
 * RestTemplateCustomizer that registers the {@link ProblemResponseErrorHandler}.
 *
 * @see ProblemResponseErrorHandler
 */
public class ProblemRestTemplateCustomizer extends AbstractProblemRestTemplateCustomizer
        implements RestTemplateCustomizer {

    public ProblemRestTemplateCustomizer(ProblemResponseErrorHandler problemResponseErrorHandler) {
        super(problemResponseErrorHandler);
    }

}
