package io.github.belgif.rest.problem.internal;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Internal utility class for converting ConstraintViolation to InputValidationIssue.
 *
 * @see ConstraintViolation
 * @see InputValidationIssue
 */
public class ConstraintViolationUtil {

    private ConstraintViolationUtil() {
    }

    public static InputValidationIssue convertToInputValidationIssue(ConstraintViolation<?> violation) {
        LinkedList<Path.Node> propertyPath = new LinkedList<>();
        Iterator<Path.Node> pathIterator = violation.getPropertyPath().iterator();
        Path.MethodNode methodNode = null;
        while (pathIterator.hasNext()) {
            Path.Node p = pathIterator.next();
            if (p.getKind() == ElementKind.METHOD) {
                methodNode = p.as(Path.MethodNode.class);
            }
            if ((p.getKind() != ElementKind.METHOD && p.getKind() != ElementKind.PARAMETER)
                    || !pathIterator.hasNext()) {
                // skip leading METHOD or PARAMETER path segments,
                // e.g. "sector" instead of "createPartner.partner.sector"
                propertyPath.add(p);
            }
        }
        InEnum in = determineSource(violation, propertyPath, methodNode);
        String name = propertyPath.stream().map(Path.Node::toString).collect(Collectors.joining("."));
        return InputValidationIssues.schemaViolation(in, name, violation.getInvalidValue(), violation.getMessage());
    }

    private static InputValidationIssue convertToInputValidationIssue(FieldError fieldError, InEnum in) {
        String name = fieldError.getField();
        String invalidValue = fieldError.getRejectedValue() != null ? fieldError.getRejectedValue().toString() : "null";
        String message = fieldError.getDefaultMessage();
        return InputValidationIssues.schemaViolation(in, name, invalidValue, message);
    }

    public static BadRequestProblem convertToBadRequestProblem(MethodArgumentNotValidException exception) {
        InEnum in = determineSource(exception);
        List<InputValidationIssue> issues = exception.getFieldErrors().stream().map(fieldError ->
                convertToInputValidationIssue(fieldError, in)).collect(Collectors.toList());
        return new BadRequestProblem(issues);
    }

    private static InEnum determineSource(MethodArgumentNotValidException exception) {
        //TODO check if this logic also works when not having the @Valid in the controller, but purely using bean validation in model class.
        List<Annotation> annotations = new ArrayList<>(Arrays.asList(exception.getParameter().getParameterAnnotations()));
        if (annotations.stream().map(Annotation::annotationType).anyMatch(annotationType -> annotationType.equals(RequestParam.class))) {
            return InEnum.QUERY;
        } else if (annotations.stream().map(Annotation::annotationType).anyMatch(annotationType -> annotationType.equals(PathVariable.class))) {
            return InEnum.PATH;
        } else if (annotations.stream().map(Annotation::annotationType).anyMatch(annotationType -> annotationType.equals(RequestHeader.class))) {
            return InEnum.HEADER;
        } else if (annotations.stream().map(Annotation::annotationType).anyMatch(annotationType -> annotationType.equals(RequestBody.class))) {
            return InEnum.BODY;
        } else {
            //TODO do something sensible
            throw new RuntimeException("Something is wrong");
        }
    }

    private static InEnum determineSource(ConstraintViolation<?> violation,
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
