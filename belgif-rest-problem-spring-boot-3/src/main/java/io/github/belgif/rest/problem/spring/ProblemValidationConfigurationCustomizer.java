package io.github.belgif.rest.problem.spring;

import jakarta.validation.Configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.validation.ValidationConfigurationCustomizer;
import org.springframework.stereotype.Component;

/**
 * ValidationConfigurationCustomizer that registers the AnnotationParameterNameProvider.
 */
@Component
@ConditionalOnClass(Configuration.class)
public class ProblemValidationConfigurationCustomizer extends AbstractProblemValidationConfigurationCustomizer
        implements ValidationConfigurationCustomizer {

}
