package io.github.belgif.rest.problem.spring;

import jakarta.validation.Configuration;

import org.springframework.boot.autoconfigure.validation.ValidationConfigurationCustomizer;
import org.springframework.stereotype.Component;

/**
 * ValidationConfigurationCustomizer that registers the AnnotationParameterNameProvider.
 */
@Component
public class ProblemValidationConfigurationCustomizer implements ValidationConfigurationCustomizer {

    @Override
    public void customize(Configuration<?> configuration) {
        configuration.parameterNameProvider(new AnnotationParameterNameProvider());
    }

}
