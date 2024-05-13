package io.github.belgif.rest.problem.spring;

import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssues;
import io.github.belgif.rest.problem.api.Problem;

/**
 * RestController exception handler for routing-related exceptions.
 */
@RestControllerAdvice
@Order(1)
public class RoutingExceptionsHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Problem> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException exception) {
        String name = exception.getParameterName();
        String detail = exception.getMessage();
        return ProblemMediaType.INSTANCE
                .toResponse(new BadRequestProblem(InputValidationIssues.schemaViolation(InEnum.QUERY, name, null,
                        detail)));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Problem> handleMissingRequestHeaderException(MissingRequestHeaderException exception) {
        String name = exception.getHeaderName();
        String detail = exception.getMessage();
        return ProblemMediaType.INSTANCE
                .toResponse(new BadRequestProblem(InputValidationIssues.schemaViolation(InEnum.HEADER, name, null,
                        detail)));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Problem> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        InEnum in = InEnum.BODY;
        String detail = exception.getMessage();
        return ProblemMediaType.INSTANCE
                .toResponse(new BadRequestProblem(InputValidationIssues.schemaViolation(in, null, null,
                        detail)));
    }

}
