package io.github.belgif.rest.problem.spring.server;

import org.springframework.boot.autoconfigure.validation.ValidationConfigurationCustomizer;

/**
 * ValidationConfigurationCustomizer that registers the AnnotationParameterNameProvider.
 */
public class ProblemValidationConfigurationCustomizer extends AbstractProblemValidationConfigurationCustomizer
        implements ValidationConfigurationCustomizer {

}
