package io.github.belgif.rest.problem.spring.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplateCustomizer that registers the {@link ProblemResponseErrorHandler}.
 *
 * @see ProblemResponseErrorHandler
 */
@Component
@ConditionalOnClass({ RestTemplate.class, RestTemplateCustomizer.class })
public class ProblemRestTemplateCustomizer extends AbstractProblemRestTemplateCustomizer
        implements RestTemplateCustomizer {

    public ProblemRestTemplateCustomizer(ProblemResponseErrorHandler problemResponseErrorHandler) {
        super(problemResponseErrorHandler);
    }

}
