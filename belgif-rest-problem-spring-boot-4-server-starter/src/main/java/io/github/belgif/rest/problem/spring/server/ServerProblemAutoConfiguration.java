package io.github.belgif.rest.problem.spring.server;

import jakarta.validation.Configuration;
import jakarta.validation.ConstraintViolationException;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.validation.autoconfigure.ValidationConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.atlassian.oai.validator.springmvc.InvalidRequestException;

import tools.jackson.databind.ObjectMapper;

/**
 * Spring Boot AutoConfiguration for rest-problem-spring.
 */
@AutoConfiguration
@ConditionalOnWebApplication
@Import({ io.github.belgif.rest.problem.spring.ProblemJackson3Configuration.class, JacksonAutoConfiguration.class })
public class ServerProblemAutoConfiguration {

    @Bean
    public ProblemExceptionHandler problemExceptionHandler() {
        return new ProblemExceptionHandler();
    }

    @Bean
    public RoutingExceptionsJackson3Handler routingExceptionsHandler() {
        return new RoutingExceptionsJackson3Handler();
    }

    @ConditionalOnClass(ConstraintViolationException.class)
    public static class BeanValidationProblemConfiguration {
        @Bean
        public BeanValidationExceptionsHandler beanValidationExceptionsHandler() {
            return new BeanValidationExceptionsHandler();
        }
    }

    @ConditionalOnClass({ Configuration.class, ValidationConfigurationCustomizer.class })
    public static class BeanValidationConfigurationProblemConfiguration {
        @Bean
        public ProblemValidationConfigurationCustomizer problemValidationConfigurationCustomizer() {
            return new ProblemValidationConfigurationCustomizer();
        }
    }

    @ConditionalOnClass(InvalidRequestException.class)
    public static class SwaggerRequestValidationConfiguration {
        private final ObjectMapper objectMapper;

        public SwaggerRequestValidationConfiguration(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Bean
        public InvalidRequestExceptionJackson3Handler invalidRequestExceptionHandler() {
            return new InvalidRequestExceptionJackson3Handler(objectMapper);
        }
    }

}
