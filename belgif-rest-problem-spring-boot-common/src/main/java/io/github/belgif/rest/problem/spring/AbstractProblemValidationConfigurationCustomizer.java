package io.github.belgif.rest.problem.spring;

import jakarta.validation.Configuration;

/**
 * ValidationConfigurationCustomizer that registers the AnnotationParameterNameProvider.
 */
public abstract class AbstractProblemValidationConfigurationCustomizer {

    public void customize(Configuration<?> configuration) {
        configuration.parameterNameProvider(new AnnotationParameterNameProvider());
    }

}
