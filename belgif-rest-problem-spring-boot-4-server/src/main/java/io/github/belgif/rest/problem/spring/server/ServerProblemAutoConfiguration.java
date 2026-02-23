package io.github.belgif.rest.problem.spring.server;

import jakarta.validation.Configuration;
import jakarta.validation.ConstraintViolationException;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.validation.autoconfigure.ValidationConfigurationCustomizer;
import org.springframework.context.annotation.Import;

import com.atlassian.oai.validator.springmvc.InvalidRequestException;

import tools.jackson.databind.ObjectMapper;

/**
 * Spring Boot AutoConfiguration for rest-problem-spring.
 */
@AutoConfiguration
@Import({ io.github.belgif.rest.problem.spring.ProblemJackson3Configuration.class, JacksonAutoConfiguration.class })
public class ServerProblemAutoConfiguration {

    private ObjectMapper objectMapper;

    public ServerProblemAutoConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ConditionalOnClass(ConstraintViolationException.class)
    @ConditionalOnWebApplication
    public BeanValidationExceptionsHandler beanValidationExceptionsHandler() {
        return new BeanValidationExceptionsHandler();
    }

    @ConditionalOnWebApplication
    public ProblemExceptionHandler problemExceptionHandler() {
        return new ProblemExceptionHandler();
    }

    @ConditionalOnWebApplication
    public RoutingExceptionsHandler routingExceptionsHandler() {
        return new RoutingExceptionsHandler();
    }

    @ConditionalOnClass({ Configuration.class, ValidationConfigurationCustomizer.class })
    public ProblemValidationConfigurationCustomizer problemValidationConfigurationCustomizer() {
        return new ProblemValidationConfigurationCustomizer();
    }

    @ConditionalOnWebApplication
    @ConditionalOnClass(InvalidRequestException.class)
    public InvalidRequestExceptionHandler invalidRequestExceptionHandler() {
        return new InvalidRequestExceptionHandler(objectMapper);
    }
}
