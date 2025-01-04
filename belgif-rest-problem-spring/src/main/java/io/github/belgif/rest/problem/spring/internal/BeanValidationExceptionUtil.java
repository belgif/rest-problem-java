package io.github.belgif.rest.problem.spring.internal;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path.MethodNode;
import javax.validation.Path.Node;
import javax.validation.constraints.NotNull;

import org.springframework.validation.FieldError;

import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

/**
 * Internal utility class for converting ConstraintViolation to InputValidationIssue.
 *
 * @see ConstraintViolation
 * @see InputValidationIssue
 */
public class BeanValidationExceptionUtil {

    private BeanValidationExceptionUtil() {
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
        InEnum in = DetermineSourceUtil.determineSource(violation, propertyPath, methodNode);
        String name = propertyPath.stream().map(Node::toString).collect(Collectors.joining("."));
        return InputValidationIssues.schemaViolation(in, name, violation.getInvalidValue(), violation.getMessage());
    }

    public static InputValidationIssue convertToInputValidationIssue(@NotNull FieldError fieldError, InEnum in) {
        String invalidValue = Objects.toString(fieldError.getRejectedValue(), null);
        return InputValidationIssues.schemaViolation(in, fieldError.getField(), invalidValue,
                fieldError.getDefaultMessage());
    }

}
