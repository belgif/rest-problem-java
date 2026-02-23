package io.github.belgif.rest.problem.spring.client;

import org.springframework.web.client.RestTemplate;

/**
 * RestTemplateCustomizer that registers the {@link AbstractProblemResponseErrorHandler}.
 *
 * @see AbstractProblemResponseErrorHandler
 */
public abstract class AbstractProblemRestTemplateCustomizer {

    private final AbstractProblemResponseErrorHandler problemResponseErrorHandler;

    protected AbstractProblemRestTemplateCustomizer(AbstractProblemResponseErrorHandler problemResponseErrorHandler) {
        this.problemResponseErrorHandler = problemResponseErrorHandler;
    }

    public void customize(RestTemplate restTemplate) {
        restTemplate.setErrorHandler(problemResponseErrorHandler);
    }

}
