package io.github.belgif.rest.problem.spring.client;

import org.springframework.boot.restclient.RestTemplateCustomizer;
import org.springframework.web.client.RestTemplate;

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
