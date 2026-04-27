package io.github.belgif.rest.problem.spring.boot.client;

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
import io.github.belgif.rest.problem.spring.client.ProblemResponseErrorHandler;
import io.github.belgif.rest.problem.spring.client.ProblemResponseJackson2ErrorHandler;

/**
 * Spring Boot AutoConfiguration for rest-problem-spring.
 */
@AutoConfiguration
@Import({ ProblemJackson2Configuration.class, JacksonAutoConfiguration.class })
public class ClientProblemAutoConfiguration {

    @Bean
    public ProblemResponseErrorHandler problemResponseErrorHandler(ObjectMapper objectMapper) {
        return new ProblemResponseJackson2ErrorHandler(objectMapper);
    }

    @ConditionalOnClass({ RestClient.class, RestClientCustomizer.class })
    public static class RestClientProblemConfiguration {
        @Bean
        public ProblemRestClientCustomizer
                problemRestClientCustomizer(ProblemResponseErrorHandler problemResponseErrorHandler) {
            return new ProblemRestClientCustomizer(problemResponseErrorHandler);
        }
    }

    @ConditionalOnClass({ RestTemplate.class, RestTemplateCustomizer.class })
    public static class RestTemplateProblemConfiguration {
        @Bean
        public ProblemRestTemplateCustomizer
                problemRestTemplateCustomizer(ProblemResponseErrorHandler problemResponseErrorHandler) {
            return new ProblemRestTemplateCustomizer(problemResponseErrorHandler);
        }
    }

    @ConditionalOnClass({ WebClient.class, WebClientCustomizer.class })
    public static class WebClientProblemConfiguration {
        @Bean
        public ProblemWebClientCustomizer problemWebClientCustomizer() {
            return new ProblemWebClientCustomizer();
        }
    }

}
