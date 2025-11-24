package io.github.belgif.rest.problem.ee.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path.MethodNode;
import javax.validation.Path.Node;
import javax.validation.Path.ParameterNode;
import javax.ws.rs.BeanParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import io.github.belgif.rest.problem.api.InEnum;
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

    private static final Map<Class<? extends Annotation>, InEnum> SOURCE_MAPPING = new HashMap<>();

    static {
        SOURCE_MAPPING.put(QueryParam.class, InEnum.QUERY);
        SOURCE_MAPPING.put(PathParam.class, InEnum.PATH);
        SOURCE_MAPPING.put(HeaderParam.class, InEnum.HEADER);
    }

    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] ANNOTATIONS = SOURCE_MAPPING.keySet().toArray(new Class[0]);

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
        InEnum in = determineSource(violation, propertyPath, methodNode);
        String name = String.join(".", propertyName);
        if (in == InEnum.BODY && propertyPath.get(propertyPath.size() - 1).getKind() == ElementKind.PARAMETER) {
            name = null;
        }
        return InputValidationIssues.schemaViolation(in, name, violation.getInvalidValue(), violation.getMessage());
    }

    private static InEnum determineSource(ConstraintViolation<?> violation, List<Node> propertyPath,
            MethodNode methodNode) {
        Node last = propertyPath.get(propertyPath.size() - 1);
        Node parent = propertyPath.size() > 1 ? propertyPath.get(propertyPath.size() - 2) : null;
        if (last.getKind() == ElementKind.PARAMETER) {
            if (methodNode != null) {
                ParameterNode param = last.as(ParameterNode.class);
                Method method = findMethod(violation.getRootBeanClass(), methodNode);
                return AnnotationUtil.findParamAnnotation(method, param.getParameterIndex(), ANNOTATIONS)
                        .map(Annotation::annotationType).map(SOURCE_MAPPING::get)
                        .orElse(InEnum.BODY);
            } else {
                return InEnum.QUERY;
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
                        if (SOURCE_MAPPING.containsKey(annotation.annotationType())) {
                            return SOURCE_MAPPING.get(annotation.annotationType());
                        }
                    }
                    return InEnum.BODY;
                } catch (NoSuchFieldException e) {
                    throw new IllegalStateException("Field " + last.getName() + " not found on " + beanParamClass, e);
                }
            }
        }
        return InEnum.BODY;
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
