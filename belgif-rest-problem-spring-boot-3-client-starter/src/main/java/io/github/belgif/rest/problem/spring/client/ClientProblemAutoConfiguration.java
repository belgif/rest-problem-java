package io.github.belgif.rest.problem.spring.client;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.belgif.rest.problem.spring.ProblemJackson2Configuration;

/**
 * Spring Boot AutoConfiguration for rest-problem-spring.
 */
@AutoConfiguration
@Import({ ProblemJackson2Configuration.class, JacksonAutoConfiguration.class,
        ProblemResponseJackson2ErrorHandler.class })
public class ClientProblemAutoConfiguration {

    private ObjectMapper objectMapper;

    private ProblemResponseJackson2ErrorHandler problemResponseErrorHandler;

    public ClientProblemAutoConfiguration(ObjectMapper objectMapper,
            ProblemResponseJackson2ErrorHandler problemResponseErrorHandler) {
        this.objectMapper = objectMapper;
        this.problemResponseErrorHandler = problemResponseErrorHandler;
    }

    @ConditionalOnClass({ RestClient.class, RestClientCustomizer.class })
    @Bean
    public ProblemRestClientCustomizer problemRestClientCustomizer() {
        return new ProblemRestClientCustomizer(problemResponseErrorHandler);
    }

    @ConditionalOnClass({ RestTemplate.class, RestTemplateCustomizer.class })
    @Bean
    public ProblemRestTemplateCustomizer problemRestTemplateCustomizer() {
        return new ProblemRestTemplateCustomizer(problemResponseErrorHandler);
    }

    @ConditionalOnClass({ WebClient.class, WebClientCustomizer.class })
    @Bean
    public ProblemWebClientCustomizer problemWebClientCustomizer() {
        return new ProblemWebClientCustomizer();
    }
}
