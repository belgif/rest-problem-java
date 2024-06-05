package io.github.belgif.rest.problem.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path.MethodNode;
import javax.validation.Path.Node;
import javax.validation.Path.ParameterNode;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

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
        LinkedList<Node> propertyPath = new LinkedList<>();
        Iterator<Node> pathIterator = violation.getPropertyPath().iterator();
        MethodNode methodNode = null;
        while (pathIterator.hasNext()) {
            Node p = pathIterator.next();
            if (p.getKind() == ElementKind.METHOD) {
                methodNode = p.as(MethodNode.class);
            }
            if ((p.getKind() != ElementKind.METHOD && p.getKind() != ElementKind.PARAMETER)
                    || !pathIterator.hasNext()) {
                // skip leading METHOD or PARAMETER path segments,
                // e.g. "sector" instead of "createPartner.partner.sector"
                propertyPath.add(p);
            }
        }
        InEnum in = determineSource(violation, propertyPath, methodNode);
        String name = propertyPath.stream().map(Node::toString).collect(Collectors.joining("."));
        return InputValidationIssues.schemaViolation(in, name, violation.getInvalidValue(), violation.getMessage());
    }

    private static InEnum determineSource(ConstraintViolation<?> violation,
            LinkedList<Node> propertyPath, MethodNode methodNode) {
        if (propertyPath.getLast().getKind() == ElementKind.PARAMETER) {
            if (methodNode != null) {
                ParameterNode param = propertyPath.getLast().as(ParameterNode.class);
                try {
                    Method method = violation.getRootBeanClass().getMethod(methodNode.getName(),
                            methodNode.getParameterTypes().toArray(new Class[0]));
                    return AnnotationUtil.findParamAnnotation(method, param.getParameterIndex(), ANNOTATIONS)
                            .map(Annotation::annotationType).map(SOURCE_MAPPING::get)
                            .orElse(InEnum.BODY);
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

}
