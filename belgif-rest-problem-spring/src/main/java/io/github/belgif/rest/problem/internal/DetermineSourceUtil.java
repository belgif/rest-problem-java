package io.github.belgif.rest.problem.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;

import io.github.belgif.rest.problem.api.InEnum;

public class DetermineSourceUtil {

    public static InEnum determineSource(Annotation[] annotations) {
        List<Annotation> annotationList =
                new ArrayList<>(Arrays.asList(annotations));
        if (annotationList.stream().map(Annotation::annotationType)
                .anyMatch(annotationType -> annotationType.equals(PathVariable.class))) {
            return InEnum.PATH;
        } else if (annotationList.stream().map(Annotation::annotationType)
                .anyMatch(annotationType -> annotationType.equals(RequestHeader.class))) {
            return InEnum.HEADER;
        } else if (annotationList.stream().map(Annotation::annotationType)
                .anyMatch(annotationType -> annotationType.equals(RequestBody.class))) {
            return InEnum.BODY;
        } else {
            return InEnum.QUERY;
        }
    }

    public static InEnum determineSource(ServletWebRequest request, String parameterName) {
        HandlerMethod handlerMethod = getHandlerMethod(request);
        if (handlerMethod != null) {
            Method method = handlerMethod.getMethod();
            List<Parameter> parameters = new ArrayList<>(Arrays.asList(method.getParameters()));
            Optional<Parameter> parameter =
                    parameters.stream().filter(param -> param.getName().equals(parameterName)).findAny();
            if (parameter.isPresent()) {
                Annotation[] annotations = parameter.get().getAnnotations();
                return determineSource(annotations);
            }
            parameter = parameters.stream()
                    .filter(param -> param.getType().getSimpleName().equalsIgnoreCase(parameterName)).findAny();
            if (parameter.isPresent()) {
                Annotation[] annotations = parameter.get().getAnnotations();
                return determineSource(annotations);
            }
        }
        return InEnum.QUERY;
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

    public static InEnum determineSource(ConstraintViolation<?> violation,
            LinkedList<Path.Node> propertyPath, Path.MethodNode methodNode) {
        if (propertyPath.getLast().getKind() == ElementKind.PARAMETER) {
            if (methodNode != null) {
                Path.ParameterNode param = propertyPath.getLast().as(Path.ParameterNode.class);
                try {
                    Method method = violation.getRootBeanClass().getMethod(methodNode.getName(),
                            methodNode.getParameterTypes().toArray(new Class[0]));
                    if (hasParamAnnotation(method, param.getParameterIndex(), RequestParam.class)) {
                        return InEnum.QUERY;
                    } else if (hasParamAnnotation(method, param.getParameterIndex(), PathVariable.class)) {
                        return InEnum.PATH;
                    } else if (hasParamAnnotation(method, param.getParameterIndex(), RequestHeader.class)) {
                        return InEnum.HEADER;
                    } else {
                        return InEnum.QUERY;
                    }
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

    private static boolean hasParamAnnotation(Method m, int ix, Class<? extends Annotation> ann) {
        if (m.getParameters()[ix].isAnnotationPresent(ann)) {
            return true;
        }
        Class<?> superclass = m.getDeclaringClass().getSuperclass();
        try {
            if (superclass != null && superclass != Object.class
                    && hasParamAnnotation(superclass.getMethod(m.getName(), m.getParameterTypes()), ix, ann)) {
                return true;
            }
        } catch (NoSuchMethodException e) {
            // ignore
        }
        Class<?>[] interfaces = m.getDeclaringClass().getInterfaces();
        for (Class<?> intf : interfaces) {
            try {
                if (hasParamAnnotation(intf.getMethod(m.getName(), m.getParameterTypes()), ix, ann)) {
                    return true;
                }
            } catch (NoSuchMethodException e) {
                // ignore
            }
        }
        return false;
    }

}
