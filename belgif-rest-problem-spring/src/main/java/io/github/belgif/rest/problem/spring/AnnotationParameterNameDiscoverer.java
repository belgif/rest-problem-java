package io.github.belgif.rest.problem.spring;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ParameterNameDiscoverer that retrieves the parameter name from Spring MVC annotations (if present).
 */
public class AnnotationParameterNameDiscoverer implements ParameterNameDiscoverer {

    private final ConcurrentHashMap<Executable, String[]> parameterNameCache = new ConcurrentHashMap<>();

    @Override
    public String[] getParameterNames(Constructor<?> constructor) {
        return parameterNameCache.computeIfAbsent(constructor, this::getParameterNames);
    }

    @Override
    public String[] getParameterNames(Method method) {
        return parameterNameCache.computeIfAbsent(method, this::getParameterNames);
    }

    private String[] getParameterNames(Executable executable) {
        Parameter[] parameters = executable.getParameters();
        String[] parameterNames = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            parameterNames[i] = getParameterName(parameters[i]);
        }
        return parameterNames;
    }

    private String getParameterName(Parameter parameter) {
        Annotation[] annotations = parameter.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof RequestParam) {
                return ((RequestParam) annotation).value();
            } else if (annotation instanceof PathVariable) {
                return ((PathVariable) annotation).value();
            } else if (annotation instanceof RequestHeader) {
                return ((RequestHeader) annotation).value();
            } else if (annotation instanceof CookieValue) {
                return ((CookieValue) annotation).value();
            } else if (annotation instanceof MatrixVariable) {
                return ((MatrixVariable) annotation).value();
            }
        }
        return parameter.getName();
    }

}
