package io.github.belgif.rest.problem.spring;

import org.springframework.boot.restclient.RestTemplateCustomizer;
import org.springframework.stereotype.Component;

/**
 * RestTemplateCustomizer that registers the {@link ProblemResponseErrorHandler}.
 *
 * @see ProblemResponseErrorHandler
 */
@Component
public class ProblemRestTemplateCustomizer extends AbstractProblemRestTemplateCustomizer
        implements RestTemplateCustomizer {

    public ProblemRestTemplateCustomizer(ProblemResponseErrorHandler problemResponseErrorHandler) {
        super(problemResponseErrorHandler);
    }

}
