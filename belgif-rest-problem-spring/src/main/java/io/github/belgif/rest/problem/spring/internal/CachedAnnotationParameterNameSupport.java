package io.github.belgif.rest.problem.spring.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Helper class for cached retrieval of parameter names from Spring MVC annotations.
 *
 * @param <T> the result type
 */
public abstract class CachedAnnotationParameterNameSupport<T> {

    private final ConcurrentHashMap<Executable, T> parameterNameCache = new ConcurrentHashMap<>();

    public T getParameterNames(@NonNull Constructor<?> constructor) {
        return parameterNameCache.computeIfAbsent(constructor, this::getParameterNames);
    }

    public T getParameterNames(@NonNull Method method) {
        return parameterNameCache.computeIfAbsent(method, this::getParameterNames);
    }

    private T getParameterNames(Executable executable) {
        return toResult(Arrays.stream(executable.getParameters())
                .map(this::getParameterName));
    }

    private String getParameterName(Parameter parameter) {
        String parameterName = getParameterNameFromAnnotations(parameter);
        if (parameterName == null || parameterName.isEmpty()) {
            parameterName = parameter.getName();
        }
        return parameterName;
    }

    private String getParameterNameFromAnnotations(Parameter parameter) {
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
        return null;
    }

    protected abstract T toResult(Stream<String> parameterNames);

}
