package io.github.belgif.rest.problem.spring.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClientCustomizer that registers a filter that converts problem responses to Problem exceptions.
 */
@Component
@ConditionalOnClass({ WebClient.class, WebClientCustomizer.class })
public class ProblemWebClientCustomizer extends AbstractProblemWebClientCustomizer implements WebClientCustomizer {

}
