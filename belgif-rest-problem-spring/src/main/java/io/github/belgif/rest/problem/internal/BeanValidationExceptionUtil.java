package io.github.belgif.rest.problem.internal;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;

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
        InEnum in = DetermineSourceUtil.determineSource(violation, propertyPath, methodNode);
        String name = propertyPath.stream().map(Path.Node::toString).collect(Collectors.joining("."));
        return InputValidationIssues.schemaViolation(in, name, violation.getInvalidValue(), violation.getMessage());
    }

    public static InputValidationIssue convertToInputValidationIssue(FieldError fieldError, InEnum in) {
        String invalidValue = fieldError.getRejectedValue() != null ? fieldError.getRejectedValue().toString() : null;
        return InputValidationIssues.schemaViolation(in, fieldError.getField(), invalidValue,
                fieldError.getDefaultMessage());
    }

}
