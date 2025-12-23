package io.github.belgif.rest.problem.spring;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.validation.ParameterNameProvider;

import io.github.belgif.rest.problem.spring.internal.CachedAnnotationParameterNameSupport;

/**
 * ParameterNameProvider that retrieves the parameter name from Spring MVC annotations (if present).
 */
public class AnnotationParameterNameProvider extends CachedAnnotationParameterNameSupport<List<String>>
        implements ParameterNameProvider {

    @Override
    protected List<String> toResult(Stream<String> parameterNames) {
        return parameterNames.collect(Collectors.toList());
    }

}
