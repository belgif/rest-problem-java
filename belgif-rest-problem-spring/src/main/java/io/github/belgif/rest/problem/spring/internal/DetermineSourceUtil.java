package io.github.belgif.rest.problem.spring.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path.MethodNode;
import javax.validation.Path.Node;
import javax.validation.Path.ParameterNode;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;

import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.internal.AnnotationUtil;

public class DetermineSourceUtil {

    private static final Map<Class<? extends Annotation>, InEnum> SOURCE_MAPPING = new HashMap<>();

    static {
        SOURCE_MAPPING.put(RequestParam.class, InEnum.QUERY);
        SOURCE_MAPPING.put(PathVariable.class, InEnum.PATH);
        SOURCE_MAPPING.put(RequestHeader.class, InEnum.HEADER);
        SOURCE_MAPPING.put(RequestBody.class, InEnum.BODY);
        SOURCE_MAPPING.put(MatrixVariable.class, InEnum.PATH);
        SOURCE_MAPPING.put(CookieValue.class, InEnum.HEADER);
    }

    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] ANNOTATIONS = SOURCE_MAPPING.keySet().toArray(new Class[0]);

    private DetermineSourceUtil() {
    }

    public static InEnum determineSource(ServletWebRequest request, String parameterName) {
        HandlerMethod handlerMethod = getHandlerMethod(request);
        if (handlerMethod != null) {
            return determineSource(handlerMethod.getMethod(), parameterName);
        } else {
            return InEnum.QUERY;
        }
    }

    public static InEnum determineSource(Parameter parameter) {
        return determineSource((Method) parameter.getDeclaringExecutable(), parameter.getName());
    }

    private static InEnum determineSource(Method method, String parameterName) {
        Optional<Annotation> annotation = AnnotationUtil.findParamAnnotation(method, parameterName, ANNOTATIONS);
        return annotation.map(Annotation::annotationType).map(SOURCE_MAPPING::get).orElse(InEnum.QUERY);
    }

    public static InEnum determineSource(Method method, int parameterIndex) {
        Optional<Annotation> annotation = AnnotationUtil.findParamAnnotation(method, parameterIndex, ANNOTATIONS);
        return annotation.map(Annotation::annotationType).map(SOURCE_MAPPING::get).orElse(InEnum.QUERY);
    }

    public static InEnum determineSource(ConstraintViolation<?> violation,
            LinkedList<Node> propertyPath, MethodNode methodNode) {
        if (propertyPath.getLast().getKind() == ElementKind.PARAMETER) {
            if (methodNode != null) {
                ParameterNode param = propertyPath.getLast().as(ParameterNode.class);
                try {
                    Method method = violation.getRootBeanClass().getMethod(methodNode.getName(),
                            methodNode.getParameterTypes().toArray(new Class[0]));
                    return determineSource(method, param.getParameterIndex());
                } catch (NoSuchMethodException e) {
                    throw new IllegalStateException(
                            "Method " + methodNode.getName() + " not found on " + violation.getRootBeanClass(), e);
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
