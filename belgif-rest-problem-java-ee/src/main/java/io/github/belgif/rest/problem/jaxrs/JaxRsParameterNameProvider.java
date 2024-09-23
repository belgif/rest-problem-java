package io.github.belgif.rest.problem.jaxrs;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.ParameterNameProvider;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ext.Provider;

/**
 * A ParameterNameProvider that retrieves the parameter name from JAX-RS annotations (if present).
 *
 * @see ParameterNameProvider
 */
@Provider
public class JaxRsParameterNameProvider implements ParameterNameProvider {

    private final ConcurrentHashMap<Executable, List<String>> parameterNameCache = new ConcurrentHashMap<>();

    @Override
    public List<String> getParameterNames(Constructor<?> constructor) {
        return parameterNameCache.computeIfAbsent(constructor, this::getParameterNames);
    }

    @Override
    public List<String> getParameterNames(Method method) {
        return parameterNameCache.computeIfAbsent(method, this::getParameterNames);
    }

    private List<String> getParameterNames(Executable executable) {
        Parameter[] parameters = executable.getParameters();
        List<String> parameterNames = new ArrayList<>(parameters.length);
        for (Parameter parameter : parameters) {
            parameterNames.add(getParameterName(parameter));
        }
        return parameterNames;
    }

    private String getParameterName(Parameter parameter) {
        Annotation[] annotations = parameter.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof QueryParam) {
                return ((QueryParam) annotation).value();
            } else if (annotation instanceof PathParam) {
                return ((PathParam) annotation).value();
            } else if (annotation instanceof HeaderParam) {
                return ((HeaderParam) annotation).value();
            } else if (annotation instanceof CookieParam) {
                return ((CookieParam) annotation).value();
            } else if (annotation instanceof FormParam) {
                return ((FormParam) annotation).value();
            } else if (annotation instanceof MatrixParam) {
                return ((MatrixParam) annotation).value();
            }
        }
        return parameter.getName();
    }

}
