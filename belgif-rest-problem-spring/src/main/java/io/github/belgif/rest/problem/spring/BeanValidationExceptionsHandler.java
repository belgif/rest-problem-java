package io.github.belgif.rest.problem.spring;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.internal.ConstraintViolationUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Exception handler for RestControllers.
 *
 * <p>
 * Handles the response serialization for ConstraintViolation exceptions.
 * </p>
 */
@RestControllerAdvice
public class ConstraintViolationExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Problem> handleConstraintViolationException(ConstraintViolationException exception) {
        return ProblemMediaType.INSTANCE.toResponse(
                new BadRequestProblem(exception.getConstraintViolations().stream()
                        .map(ConstraintViolationUtil::convertToInputValidationIssue)
                        .sorted(Comparator.comparing(InputValidationIssue::getName))
                        .collect(Collectors.toList()))
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Problem> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return ProblemMediaType.INSTANCE.toResponse(
                ConstraintViolationUtil.convertToBadRequestProblem(exception)
        );
    }

}
