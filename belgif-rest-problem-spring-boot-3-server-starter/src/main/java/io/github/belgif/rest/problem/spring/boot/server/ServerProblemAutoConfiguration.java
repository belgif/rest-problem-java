package io.github.belgif.rest.problem.spring.boot.server;

import jakarta.validation.Configuration;
import jakarta.validation.ConstraintViolationException;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.atlassian.oai.validator.springmvc.InvalidRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.belgif.rest.problem.spring.server.BeanValidationExceptionsHandler;
import io.github.belgif.rest.problem.spring.server.InvalidRequestExceptionJackson2Handler;
import io.github.belgif.rest.problem.spring.server.ProblemExceptionHandler;
import io.github.belgif.rest.problem.spring.server.RoutingExceptionsJackson2Handler;

/**
 * Spring Boot AutoConfiguration for rest-problem-spring.
 */
@AutoConfiguration
@ConditionalOnWebApplication
@Import({ io.github.belgif.rest.problem.spring.ProblemJackson2Configuration.class, JacksonAutoConfiguration.class })
public class ServerProblemAutoConfiguration {

    @Bean
    public ProblemExceptionHandler problemExceptionHandler() {
        return new ProblemExceptionHandler();
    }

    @Bean
    public RoutingExceptionsJackson2Handler routingExceptionsHandler() {
        return new RoutingExceptionsJackson2Handler();
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
    public static class SwaggerValidatorProblemConfiguration {
        private final ObjectMapper objectMapper;

        public SwaggerValidatorProblemConfiguration(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Bean
        public InvalidRequestExceptionJackson2Handler invalidRequestExceptionHandler() {
            return new InvalidRequestExceptionJackson2Handler(objectMapper);
        }
    }

}
