package io.github.belgif.rest.problem.spring;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolationException;

import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.internal.BeanValidationExceptionUtil;
import io.github.belgif.rest.problem.internal.DetermineSourceUtil;

/**
 * Exception handler for RestControllers.
 *
 * <p>
 * Handles the response serialization for ConstraintViolation and MethodArgumentNotValid exceptions.
 * </p>
 */
@RestControllerAdvice
@Order(1)
public class BeanValidationExceptionsHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Problem> handleConstraintViolationException(ConstraintViolationException exception) {
        return ProblemMediaType.INSTANCE.toResponse(
                new BadRequestProblem(exception.getConstraintViolations().stream()
                        .map(BeanValidationExceptionUtil::convertToInputValidationIssue)
                        .sorted(Comparator.comparing(InputValidationIssue::getName))
                        .collect(Collectors.toList())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Problem> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        InEnum in = DetermineSourceUtil.determineSource(exception.getParameter().getParameterAnnotations());
        List<InputValidationIssue> issues = exception.getFieldErrors().stream()
                .map(fieldError -> BeanValidationExceptionUtil.convertToInputValidationIssue(fieldError, in))
                .collect(Collectors.toList());
        return ProblemMediaType.INSTANCE.toResponse(new BadRequestProblem(issues));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Problem> handleBindException(BindException exception, ServletWebRequest request) {
        InEnum in = DetermineSourceUtil.determineSource(request, exception.getObjectName());
        List<InputValidationIssue> issues = exception.getFieldErrors().stream()
                .map(fieldError -> BeanValidationExceptionUtil.convertToInputValidationIssue(fieldError, in))
                .collect(Collectors.toList());
        return ProblemMediaType.INSTANCE.toResponse(new BadRequestProblem(issues));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Problem>
            handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        InEnum in = DetermineSourceUtil.determineSource(exception.getParameter().getParameterAnnotations());
        String name = exception.getName();
        String detail;
        if (exception.getRequiredType() != null) {
            detail = name + " should be of type " + exception.getRequiredType().getSimpleName();
        } else {
            detail = name + " of incorrect type";
        }
        String invalidValue = (String) exception.getValue();
        return ProblemMediaType.INSTANCE
                .toResponse(new BadRequestProblem(InputValidationIssues.schemaViolation(in, name, invalidValue,
                        detail)));
    }

}
