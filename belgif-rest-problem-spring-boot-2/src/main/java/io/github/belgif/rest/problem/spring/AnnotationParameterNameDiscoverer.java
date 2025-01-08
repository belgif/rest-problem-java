package io.github.belgif.rest.problem.spring;

import java.util.stream.Stream;

import org.springframework.core.ParameterNameDiscoverer;

import io.github.belgif.rest.problem.spring.internal.CachedAnnotationParameterNameSupport;

/**
 * ParameterNameDiscoverer that retrieves the parameter name from Spring MVC annotations (if present).
 */
public class AnnotationParameterNameDiscoverer extends CachedAnnotationParameterNameSupport<String[]>
        implements ParameterNameDiscoverer {

    @Override
    protected String[] toResult(Stream<String> parameterNames) {
        return parameterNames.toArray(String[]::new);
    }

}
