package io.github.belgif.rest.problem.spring.boot.server;

import jakarta.validation.Configuration;

import org.springframework.boot.autoconfigure.validation.ValidationConfigurationCustomizer;

import io.github.belgif.rest.problem.spring.server.AnnotationParameterNameProvider;

/**
 * ValidationConfigurationCustomizer that registers the AnnotationParameterNameProvider.
 */
public class ProblemValidationConfigurationCustomizer implements ValidationConfigurationCustomizer {

    public void customize(Configuration<?> configuration) {
        configuration.parameterNameProvider(new AnnotationParameterNameProvider());
    }
}
