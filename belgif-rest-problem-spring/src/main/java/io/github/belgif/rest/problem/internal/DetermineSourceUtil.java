package io.github.belgif.rest.problem.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ElementKind;
import jakarta.validation.Path.MethodNode;
import jakarta.validation.Path.Node;
import jakarta.validation.Path.ParameterNode;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;

import io.github.belgif.rest.problem.api.InEnum;

public class DetermineSourceUtil {

    private DetermineSourceUtil() {
    }

    public static InEnum determineSource(Annotation[] annotations) {
        Set<Class<? extends Annotation>> annotationTypes =
                Arrays.stream(annotations).map(Annotation::annotationType).collect(Collectors.toSet());
        if (annotationTypes.contains(PathVariable.class)) {
            return InEnum.PATH;
        } else if (annotationTypes.contains(RequestHeader.class)) {
            return InEnum.HEADER;
        } else if (annotationTypes.contains(RequestBody.class)) {
            return InEnum.BODY;
        } else {
            return InEnum.QUERY;
        }
    }

    public static InEnum determineSource(ServletWebRequest request, String parameterName) {
        HandlerMethod handlerMethod = getHandlerMethod(request);
        if (handlerMethod != null) {
            return determineSource(handlerMethod.getMethod(), parameterName);
        } else {
            return InEnum.QUERY;
        }
    }

    public static InEnum determineSource(Method method, String parameterName) {
        List<Parameter> parameters = new ArrayList<>(Arrays.asList(method.getParameters()));
        if (parameterName != null) {
            Optional<Parameter> parameter =
                    parameters.stream().filter(param -> param.getName().equals(parameterName)).findAny();
            if (parameter.isPresent()) {
                Annotation[] annotations = parameter.get().getAnnotations();
                return determineSource(annotations);
            }
        }
        if (parameters.size() == 1) {
            Annotation[] annotations = parameters.get(0).getAnnotations();
            return determineSource(annotations);
        }
        return InEnum.QUERY;
    }

    public static InEnum determineSource(ConstraintViolation<?> violation,
            LinkedList<Node> propertyPath, MethodNode methodNode) {
        if (propertyPath.getLast().getKind() == ElementKind.PARAMETER) {
            if (methodNode != null) {
                ParameterNode param = propertyPath.getLast().as(ParameterNode.class);
                try {
                    Method method = violation.getRootBeanClass().getMethod(methodNode.getName(),
                            methodNode.getParameterTypes().toArray(new Class[0]));
                    return determineSource(method, param.getName());
                } catch (NoSuchMethodException e) {
                    return InEnum.QUERY;
                }
            } else {
                return InEnum.QUERY;
            }
        } else {
            return InEnum.BODY;
        }
    }

    private static HandlerMethod getHandlerMethod(ServletWebRequest request) {
        Enumeration<String> attributeNames = request.getRequest().getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            Object attribute = request.getRequest().getAttribute(attributeNames.nextElement());
            if (attribute instanceof HandlerMethod) {
                return (HandlerMethod) attribute;
            }
        }
        return null;
    }

}
