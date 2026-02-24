package io.github.belgif.rest.problem.spring.client;

import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClientCustomizer that registers a filter that converts problem responses to Problem exceptions.
 */
public abstract class AbstractProblemWebClientCustomizer {

    public void customize(WebClient.Builder webClientBuilder) {
        webClientBuilder.filter(WebClientFilter.PROBLEM_FILTER);
    }

}
