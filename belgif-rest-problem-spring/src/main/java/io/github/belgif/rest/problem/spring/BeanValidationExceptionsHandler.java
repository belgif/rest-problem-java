package io.github.belgif.rest.problem.spring;

import java.util.Comparator;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolationException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.internal.BeanValidationExceptionUtil;

/**
 * Exception handler for RestControllers.
 *
 * <p>
 * Handles the response serialization for ConstraintViolation and MethodArgumentNotValid exceptions.
 * </p>
 */
@RestControllerAdvice
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
        return ProblemMediaType.INSTANCE.toResponse(
                BeanValidationExceptionUtil.convertToBadRequestProblem(exception));
    }

}
