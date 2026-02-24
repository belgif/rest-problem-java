package io.github.belgif.rest.problem.spring.client;

import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;

/**
 * WebClientCustomizer that registers a filter that converts problem responses to Problem exceptions.
 */
public class ProblemWebClientCustomizer extends AbstractProblemWebClientCustomizer implements WebClientCustomizer {

}
