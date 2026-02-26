package io.github.belgif.rest.problem.spring.client;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.boot.restclient.RestTemplateCustomizer;
import org.springframework.boot.webclient.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import tools.jackson.databind.ObjectMapper;

/**
 * Spring Boot AutoConfiguration for rest-problem-spring.
 */
@AutoConfiguration
@Import({ io.github.belgif.rest.problem.spring.ProblemJackson3Configuration.class, JacksonAutoConfiguration.class })
public class ClientProblemAutoConfiguration {

    @Bean
    public ProblemResponseErrorHandler problemResponseErrorHandler(ObjectMapper objectMapper) {
        return new ProblemResponseJackson3ErrorHandler(objectMapper);
    }

    @ConditionalOnClass({ RestClient.class, RestClientCustomizer.class })
    @Bean
    public ProblemRestClientCustomizer
            problemRestClientCustomizer(ProblemResponseErrorHandler problemResponseErrorHandler) {
        return new ProblemRestClientCustomizer(problemResponseErrorHandler);
    }

    @ConditionalOnClass({ RestTemplate.class, RestTemplateCustomizer.class })
    @Bean
    public ProblemRestTemplateCustomizer
            problemRestTemplateCustomizer(ProblemResponseErrorHandler problemResponseErrorHandler) {
        return new ProblemRestTemplateCustomizer(problemResponseErrorHandler);
    }

    @ConditionalOnClass({ WebClient.class, WebClientCustomizer.class })
    @Bean
    public ProblemWebClientCustomizer problemWebClientCustomizer() {
        return new ProblemWebClientCustomizer();
    }
}
