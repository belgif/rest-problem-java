package io.github.belgif.rest.problem.ee.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path.MethodNode;
import javax.validation.Path.Node;
import javax.validation.Path.ParameterNode;
import javax.ws.rs.BeanParam;

import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;
import io.github.belgif.rest.problem.internal.AnnotationUtil;

/**
 * Internal utility class for converting ConstraintViolation to InputValidationIssue.
 *
 * @see ConstraintViolation
 * @see InputValidationIssue
 */
public class ConstraintViolationUtil {

    private static final Class<? extends Annotation>[] ANNOTATIONS = ParameterSourceMapper.getAnnotations();

    private ConstraintViolationUtil() {
    }

    public static InputValidationIssue convertToInputValidationIssue(ConstraintViolation<?> violation) {
        MethodNode methodNode = null;
        List<Node> propertyPath = new ArrayList<>();
        List<String> propertyName = new ArrayList<>();
        Iterator<Node> pathIterator = violation.getPropertyPath().iterator();
        while (pathIterator.hasNext()) {
            Node p = pathIterator.next();
            propertyPath.add(p);
            if (p.getKind() == ElementKind.METHOD) {
                methodNode = p.as(MethodNode.class);
            }
            if ((p.getKind() != ElementKind.METHOD && p.getKind() != ElementKind.PARAMETER)
                    || !pathIterator.hasNext()) {
                // skip leading METHOD or PARAMETER path segments,
                // e.g. "sector" instead of "createPartner.partner.sector"
                propertyName.add(p.toString());
            }
        }
        Input<Object> input = determineInput(violation, methodNode, propertyPath, propertyName);
        return InputValidationIssues.schemaViolation(input.getIn(), input.getName(), input.getValue(),
                violation.getMessage());
    }

    private static Input<Object> determineInput(ConstraintViolation<?> violation,
            MethodNode methodNode, List<Node> propertyPath, List<String> propertyName) {
        Input<Object> input = Input.body(String.join(".", propertyName), violation.getInvalidValue());
        Node last = propertyPath.get(propertyPath.size() - 1);
        Node parent = propertyPath.size() > 1 ? propertyPath.get(propertyPath.size() - 2) : null;
        if (last.getKind() == ElementKind.PARAMETER) {
            if (methodNode != null) {
                ParameterNode param = last.as(ParameterNode.class);
                Method method = findMethod(violation.getRootBeanClass(), methodNode);
                Optional<Annotation> paramAnnotation =
                        AnnotationUtil.findParamAnnotation(method, param.getParameterIndex(), ANNOTATIONS);
                if (paramAnnotation.isPresent()) {
                    input.setIn(ParameterSourceMapper.map(paramAnnotation.get().annotationType()));
                } else {
                    input.setIn(InEnum.BODY);
                    input.setName(null);
                }
            } else {
                input.setIn(InEnum.QUERY);
            }
        } else if (last.getKind() == ElementKind.PROPERTY && parent != null
                && parent.getKind() == ElementKind.PARAMETER && methodNode != null) {
            ParameterNode param = parent.as(ParameterNode.class);
            Method method = findMethod(violation.getRootBeanClass(), methodNode);
            Optional<Annotation> beanParam =
                    AnnotationUtil.findParamAnnotation(method, param.getParameterIndex(), BeanParam.class);
            if (beanParam.isPresent()) {
                Class<?> beanParamClass = method.getParameterTypes()[param.getParameterIndex()];
                try {
                    Field field = beanParamClass.getDeclaredField(last.getName());
                    for (Annotation annotation : field.getAnnotations()) {
                        InEnum in = ParameterSourceMapper.map(annotation.annotationType());
                        if (in != null) {
                            input.setIn(in);
                            input.setName((String) annotation.annotationType().getMethod("value").invoke(annotation));
                        }
                    }
                } catch (NoSuchFieldException e) {
                    throw new IllegalStateException("Field " + last.getName() + " not found on " + beanParamClass, e);
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        return input;
    }

    private static Method findMethod(Class<?> clazz, MethodNode methodNode) {
        Class<?> current = clazz;
        // Normally Class#getMethod() recursively searches up the class hierarchy,
        // but apparantly not on Quarkus native, so we implement the recursion ourselves
        while (!Object.class.equals(current)) {
            try {
                return current.getMethod(methodNode.getName(), methodNode.getParameterTypes().toArray(new Class[0]));
            } catch (NoSuchMethodException e) {
                current = current.getSuperclass();
            }
        }
        throw new IllegalStateException("Method " + methodNode.getName() + " not found on " + clazz);
    }

}
