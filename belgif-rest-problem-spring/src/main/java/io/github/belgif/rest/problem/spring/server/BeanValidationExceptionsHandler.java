package io.github.belgif.rest.problem.spring.server;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolationException;

import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.spring.ProblemMediaType;
import io.github.belgif.rest.problem.spring.server.internal.BeanValidationExceptionUtil;
import io.github.belgif.rest.problem.spring.server.internal.DetermineSourceUtil;

/**
 * RestController exception handler for exceptions related to bean validation.
 */
@RestControllerAdvice
@Order(1)
// @Order(1) to take precedence over io.github.belgif.rest.problem.spring.ProblemExceptionHandler
public class BeanValidationExceptionsHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Problem> handleConstraintViolationException(ConstraintViolationException exception) {
        return ProblemMediaType.INSTANCE.toResponse(
                new BadRequestProblem(exception.getConstraintViolations().stream()
                        .map(BeanValidationExceptionUtil::convertToInputValidationIssue)
                        .sorted(InputValidationIssue.BY_NAME)
                        .collect(Collectors.toList())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Problem> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        InEnum in = DetermineSourceUtil.determineSource(exception.getParameter().getParameter());
        List<InputValidationIssue> issues = exception.getFieldErrors().stream()
                .map(fieldError -> BeanValidationExceptionUtil.convertToInputValidationIssue(fieldError, in))
                .sorted(Comparator.comparing(InputValidationIssue::getName))
                .collect(Collectors.toList());
        return ProblemMediaType.INSTANCE.toResponse(new BadRequestProblem(issues));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Problem> handleBindException(BindException exception, ServletWebRequest request) {
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

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Problem> handleHandlerMethodValidationException(HandlerMethodValidationException exception) {
        HandlerMethodValidationExceptionVisitor visitor = new HandlerMethodValidationExceptionVisitor();
        exception.visitResults(visitor);
        return ProblemMediaType.INSTANCE.toResponse(new BadRequestProblem(visitor.getIssues()));
    }

    static final class HandlerMethodValidationExceptionVisitor
            implements HandlerMethodValidationException.Visitor {

        private final List<InputValidationIssue> issues = new ArrayList<>();

        @Override
        public void cookieValue(CookieValue cookieValue, ParameterValidationResult result) {
            result.getResolvableErrors().forEach(error -> issues.add(
                    InputValidationIssues.schemaViolation(
                            InEnum.HEADER, cookieValue.value(), result.getArgument(), error.getDefaultMessage())));
        }

        @Override
        public void matrixVariable(MatrixVariable matrixVariable, ParameterValidationResult result) {
            result.getResolvableErrors().forEach(error -> issues.add(
                    InputValidationIssues.schemaViolation(
                            InEnum.PATH, matrixVariable.value(), result.getArgument(),
                            error.getDefaultMessage())));
        }

        @Override
        public void modelAttribute(@Nullable ModelAttribute modelAttribute, ParameterErrors errors) {
            if (modelAttribute != null) {
                errors.getResolvableErrors().forEach(error -> issues.add(
                        InputValidationIssues.schemaViolation(
                                InEnum.BODY, modelAttribute.value(), errors.getArgument(),
                                error.getDefaultMessage())));
            }
        }

        @Override
        public void pathVariable(PathVariable pathVariable, ParameterValidationResult result) {
            result.getResolvableErrors().forEach(error -> issues.add(
                    InputValidationIssues.schemaViolation(
                            InEnum.PATH, pathVariable.value(), result.getArgument(), error.getDefaultMessage())));
        }

        @Override
        public void requestBody(RequestBody requestBody, ParameterErrors errors) {
            errors.getResolvableErrors().forEach(error -> issues.add(
                    InputValidationIssues.schemaViolation(
                            InEnum.BODY, null, errors.getArgument(),
                            error.getDefaultMessage())));
        }

        @Override
        public void requestBodyValidationResult(RequestBody requestBody, ParameterValidationResult result) {
            result.getResolvableErrors().forEach(error -> issues.add(
                    InputValidationIssues.schemaViolation(
                            InEnum.BODY, null, result.getArgument(), error.getDefaultMessage())));
        }

        @Override
        public void requestHeader(RequestHeader requestHeader, ParameterValidationResult result) {
            result.getResolvableErrors().forEach(error -> issues.add(
                    InputValidationIssues.schemaViolation(
                            InEnum.HEADER, requestHeader.value(), result.getArgument(),
                            error.getDefaultMessage())));
        }

        @Override
        public void requestParam(@Nullable RequestParam requestParam, ParameterValidationResult result) {
            if (requestParam != null) {
                result.getResolvableErrors().forEach(error -> issues.add(
                        InputValidationIssues.schemaViolation(
                                InEnum.QUERY, requestParam.value(), result.getArgument(),
                                error.getDefaultMessage())));
            }
        }

        @Override
        public void requestPart(RequestPart requestPart, ParameterErrors errors) {
            errors.getResolvableErrors().forEach(error -> issues.add(
                    InputValidationIssues.schemaViolation(
                            InEnum.BODY, requestPart.value(), errors.getArgument(),
                            error.getDefaultMessage())));
        }

        @Override
        public void other(ParameterValidationResult result) {
            result.getResolvableErrors().forEach(error -> issues.add(
                    InputValidationIssues.schemaViolation(null, null, result.getArgument(),
                            error.getDefaultMessage())));

        }

        public List<InputValidationIssue> getIssues() {
            issues.sort(InputValidationIssue.BY_NAME);
            return issues;
        }

    }

}
