package io.github.belgif.rest.problem.spring;

import org.springframework.boot.restclient.RestTemplateCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplateCustomizer that registers the {@link ProblemResponseErrorHandler}.
 *
 * @see ProblemResponseErrorHandler
 */
@Component
public class ProblemRestTemplateCustomizer implements RestTemplateCustomizer {

    private final ProblemResponseErrorHandler problemResponseErrorHandler;

    public ProblemRestTemplateCustomizer(ProblemResponseErrorHandler problemResponseErrorHandler) {
        this.problemResponseErrorHandler = problemResponseErrorHandler;
    }

    @Override
    public void customize(RestTemplate restTemplate) {
        restTemplate.setErrorHandler(problemResponseErrorHandler);
    }

}
