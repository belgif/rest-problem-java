package io.github.belgif.rest.problem.spring.client;

import org.springframework.web.client.RestTemplate;

/**
 * RestTemplateCustomizer that registers the {@link ProblemResponseErrorHandler}.
 *
 * @see ProblemResponseErrorHandler
 */
public abstract class AbstractProblemRestTemplateCustomizer {

    private final ProblemResponseErrorHandler problemResponseErrorHandler;

    protected AbstractProblemRestTemplateCustomizer(ProblemResponseErrorHandler problemResponseErrorHandler) {
        this.problemResponseErrorHandler = problemResponseErrorHandler;
    }

    public void customize(RestTemplate restTemplate) {
        restTemplate.setErrorHandler(problemResponseErrorHandler);
    }

}
