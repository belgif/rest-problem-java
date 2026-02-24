package io.github.belgif.rest.problem.spring.server;

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

/**
 * Spring Boot AutoConfiguration for rest-problem-spring.
 */
@AutoConfiguration
@Import({ io.github.belgif.rest.problem.spring.ProblemJackson2Configuration.class, JacksonAutoConfiguration.class })
public class ServerProblemAutoConfiguration {

    private ObjectMapper objectMapper;

    public ServerProblemAutoConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ConditionalOnClass(ConstraintViolationException.class)
    @ConditionalOnWebApplication
    @Bean
    public BeanValidationExceptionsHandler beanValidationExceptionsHandler() {
        return new BeanValidationExceptionsHandler();
    }

    @ConditionalOnWebApplication
    @Bean
    public ProblemExceptionHandler problemExceptionHandler() {
        return new ProblemExceptionHandler();
    }

    @ConditionalOnWebApplication
    @Bean
    public RoutingExceptionsJackson2Handler routingExceptionsHandler() {
        return new RoutingExceptionsJackson2Handler();
    }

    @ConditionalOnClass({ Configuration.class, ValidationConfigurationCustomizer.class })
    @Bean
    public ProblemValidationConfigurationCustomizer problemValidationConfigurationCustomizer() {
        return new ProblemValidationConfigurationCustomizer();
    }

    @ConditionalOnWebApplication
    @ConditionalOnClass(InvalidRequestException.class)
    @Bean
    public InvalidRequestExceptionJackson2Handler invalidRequestExceptionHandler() {
        return new InvalidRequestExceptionJackson2Handler(objectMapper);
    }
}
