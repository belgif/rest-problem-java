package io.github.belgif.rest.problem.spring;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolationException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
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
import io.github.belgif.rest.problem.spring.internal.BeanValidationExceptionUtil;
import io.github.belgif.rest.problem.spring.internal.DetermineSourceUtil;
import io.github.belgif.rest.problem.spring.internal.ProblemRestControllerSupport;

/**
 * RestController exception handler for exceptions related to bean validation.
 */
@RestControllerAdvice
@ConditionalOnClass(ConstraintViolationException.class)
@ConditionalOnWebApplication
@Order(1)
// @Order(1) to take precedence over io.github.belgif.rest.problem.spring.ProblemExceptionHandler
public class BeanValidationExceptionsHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Problem> handleConstraintViolationException(ConstraintViolationException exception) {
        if (ProblemRestControllerSupport.isServerSideDisabled()) {
            throw exception;
        }
        return ProblemMediaType.INSTANCE.toResponse(
                new BadRequestProblem(exception.getConstraintViolations().stream()
                        .map(BeanValidationExceptionUtil::convertToInputValidationIssue)
                        .sorted(InputValidationIssue.BY_NAME)
                        .collect(Collectors.toList())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Problem> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception)
            throws MethodArgumentNotValidException {
        if (ProblemRestControllerSupport.isServerSideDisabled()) {
            throw exception;
        }
        InEnum in = DetermineSourceUtil.determineSource(exception.getParameter().getParameter());
        List<InputValidationIssue> issues = exception.getFieldErrors().stream()
                .map(fieldError -> BeanValidationExceptionUtil.convertToInputValidationIssue(fieldError, in))
                .sorted(Comparator.comparing(InputValidationIssue::getName))
                .collect(Collectors.toList());
        return ProblemMediaType.INSTANCE.toResponse(new BadRequestProblem(issues));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Problem> handleBindException(BindException exception, ServletWebRequest request)
            throws BindException {
        if (ProblemRestControllerSupport.isServerSideDisabled()) {
            throw exception;
        }
        List<InputValidationIssue> issues = exception.getFieldErrors().stream()
                .map(fieldError -> BeanValidationExceptionUtil.convertToInputValidationIssue(fieldError,
                        DetermineSourceUtil.determineSource(request, fieldError.getField())))
                .sorted(Comparator.comparing(InputValidationIssue::getName))
                .collect(Collectors.toList());
        return ProblemMediaType.INSTANCE.toResponse(new BadRequestProblem(issues));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Problem>
            handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        if (ProblemRestControllerSupport.isServerSideDisabled()) {
            throw exception;
        }
        InEnum in = DetermineSourceUtil.determineSource(exception.getParameter().getParameter());
        String name = exception.getName();
        Class<?> requiredType = exception.getRequiredType();
        String detail = requiredType != null ? name + " should be of type " + requiredType.getSimpleName()
                : name + " of incorrect type";
        String invalidValue = (String) exception.getValue();
        return ProblemMediaType.INSTANCE
                .toResponse(new BadRequestProblem(InputValidationIssues.schemaViolation(in, name, invalidValue,
                        detail)));
    }

}
