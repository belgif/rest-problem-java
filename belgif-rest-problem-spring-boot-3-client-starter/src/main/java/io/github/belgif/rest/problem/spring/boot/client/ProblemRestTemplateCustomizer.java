package io.github.belgif.rest.problem.spring.boot.client;

import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.web.client.RestTemplate;

import io.github.belgif.rest.problem.spring.client.ProblemResponseErrorHandler;

/**
 * RestTemplateCustomizer that registers the {@link ProblemResponseErrorHandler}.
 *
 * @see ProblemResponseErrorHandler
 */
public class ProblemRestTemplateCustomizer implements RestTemplateCustomizer {

    private final ProblemResponseErrorHandler problemResponseErrorHandler;

    public ProblemRestTemplateCustomizer(ProblemResponseErrorHandler problemResponseErrorHandler) {
        this.problemResponseErrorHandler = problemResponseErrorHandler;

    }

    public void customize(RestTemplate restTemplate) {
        restTemplate.setErrorHandler(problemResponseErrorHandler);
    }

}
