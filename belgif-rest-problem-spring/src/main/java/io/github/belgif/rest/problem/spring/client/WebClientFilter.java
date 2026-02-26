package io.github.belgif.rest.problem.spring.client;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.spring.ProblemMediaType;
import reactor.core.publisher.Mono;

public class WebClientFilter {
    public static final ExchangeFilterFunction PROBLEM_FILTER =
            ExchangeFilterFunction.ofResponseProcessor(response -> {
                MediaType mediaType = response.headers().contentType().orElse(null);
                if (ProblemMediaType.INSTANCE.isCompatibleWith(mediaType)
                        || response.statusCode().isError() && MediaType.APPLICATION_JSON.isCompatibleWith(mediaType)) {
                    return response.bodyToMono(Problem.class).flatMap(Mono::error);
                } else {
                    return Mono.just(response);
                }
            });
}
