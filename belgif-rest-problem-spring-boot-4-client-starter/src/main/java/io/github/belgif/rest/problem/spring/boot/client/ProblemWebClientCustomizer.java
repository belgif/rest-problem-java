package io.github.belgif.rest.problem.spring.boot.client;

import org.springframework.boot.webclient.WebClientCustomizer;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.belgif.rest.problem.spring.client.ProblemExchangeFilterFunction;

/**
 * WebClientCustomizer that registers a filter that converts problem responses to Problem exceptions.
 */
public class ProblemWebClientCustomizer implements WebClientCustomizer {

    private final ProblemExchangeFilterFunction exchangeFilterFunction;

    public ProblemWebClientCustomizer(ProblemExchangeFilterFunction exchangeFilterFunction) {
        this.exchangeFilterFunction = exchangeFilterFunction;
    }

    public void customize(WebClient.Builder webClientBuilder) {
        webClientBuilder.filter(exchangeFilterFunction);
    }

}
