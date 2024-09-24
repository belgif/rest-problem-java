package io.github.belgif.rest.problem.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.belgif.rest.problem.api.Problem;
import reactor.core.publisher.Mono;

/**
 * WebClientCustomizer that registers a filter that converts problem responses to Problem exceptions.
 *
 * NOTE: This class is intentionally duplicated in belgif-rest-problem-spring-boot-2,
 * to address a (<a href="https://github.com/belgif/rest-problem-java/issues/98">compatibility issue</a>).
 */
@Component
@ConditionalOnClass(WebClient.class)
public class ProblemWebClientCustomizer implements WebClientCustomizer {

    private static final ExchangeFilterFunction PROBLEM_FILTER =
            ExchangeFilterFunction.ofResponseProcessor(response -> {
                MediaType mediaType = response.headers().contentType().orElse(null);
                if (ProblemMediaType.INSTANCE.isCompatibleWith(mediaType)
                        || response.statusCode().isError() && MediaType.APPLICATION_JSON.isCompatibleWith(mediaType)) {
                    return response.bodyToMono(Problem.class).flatMap(Mono::error);
                } else {
                    return Mono.just(response);
                }
            });

    @Override
    public void customize(WebClient.Builder webClientBuilder) {
        webClientBuilder.filter(PROBLEM_FILTER);
    }

}
