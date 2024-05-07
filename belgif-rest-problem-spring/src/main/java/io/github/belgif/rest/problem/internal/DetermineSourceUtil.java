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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;

import io.github.belgif.rest.problem.api.InEnum;

public class DetermineSourceUtil {

    private DetermineSourceUtil() {
    }

    public static InEnum determineSourceOrNull(List<Annotation> annotations) {
        Set<Class<? extends Annotation>> annotationTypes =
                annotations.stream().map(Annotation::annotationType).collect(Collectors.toSet());
        if (annotationTypes.contains(PathVariable.class)) {
            return InEnum.PATH;
        } else if (annotationTypes.contains(RequestHeader.class)) {
            return InEnum.HEADER;
        } else if (annotationTypes.contains(RequestBody.class)) {
            return InEnum.BODY;
        } else if (annotationTypes.contains(RequestParam.class)) {
            return InEnum.QUERY;
        }
        return null;
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
                return determineSource(parameter.get());
            }
        }
        if (parameters.size() == 1) {
            return determineSource(parameters.get(0));
        }
        return InEnum.QUERY;
    }

    public static InEnum determineSource(Parameter parameter) {
        InEnum inEnum;
        List<Annotation> annotations = new ArrayList<>(Arrays.asList(parameter.getAnnotations()));
        inEnum = determineSourceOrNull(annotations);
        Method method = (Method) parameter.getDeclaringExecutable();
        if (inEnum == null) { // Can be null if annotation is in superclass
            Class<?> superclass = method.getDeclaringClass().getSuperclass();
            if (superclass != null && superclass != Object.class
                    && Arrays.asList(superclass.getMethods()).contains(method)) {
                annotations.addAll(getAnnotationsFromSuperParam(superclass, parameter));
            }
            inEnum = determineSourceOrNull(annotations);
        }
        if (inEnum == null) { // Can be null if annotation is in implemented interfaces
            Class<?>[] interfaces = method.getDeclaringClass().getInterfaces();
            for (Class<?> interfaceClass : interfaces) {
                annotations.addAll(getAnnotationsFromSuperParam(interfaceClass, parameter));
            }
            inEnum = determineSourceOrNull(annotations);
        }
        if (inEnum == null) { // If still null, use default
            inEnum = InEnum.QUERY;
        }
        return inEnum;
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

    private static boolean isSuperMethod(Method superMethod, Method method) {
        return superMethod.getName().equals(method.getName())
                && Arrays.equals(superMethod.getParameterTypes(), method.getParameterTypes())
                && superMethod.getReturnType().equals(method.getReturnType());
    }

    private static Parameter getSuperParameter(Method superMethod, Parameter parameter) {
        List<Parameter> params = Arrays.asList(superMethod.getParameters());
        return params.stream().filter(
                param -> param.getName().equals(parameter.getName()) && param.getType().equals(parameter.getType()))
                .findAny().orElse(null);
    }

    private static List<Annotation> getAnnotationsFromSuperParam(Class<?> superClass, Parameter parameter) {
        List<Annotation> annotations = new ArrayList<>();
        Method method = (Method) parameter.getDeclaringExecutable();
        Optional<Method> superMethod =
                Arrays.stream(superClass.getMethods()).filter(meth -> isSuperMethod(meth, method)).findAny();
        if (superMethod.isPresent()) {
            Parameter superParameter = getSuperParameter(superMethod.get(), parameter);
            if (superParameter != null) {
                annotations.addAll(Arrays.asList(superParameter.getAnnotations()));
            }
        }
        return annotations;
    }
}
