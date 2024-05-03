package io.github.belgif.rest.problem.spring;

import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssues;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.internal.DetermineSourceUtil;

/**
 * Exception handler for RestControllers.
 *
 * <p>
 * Handles the response serialization for MissingServletRequestParameterException and other routing exceptions in the
 * future
 * </p>
 */
@RestControllerAdvice
@Order(1)
public class RoutingExceptionsHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Problem> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException exception, ServletWebRequest request) {
        String name = exception.getParameterName();
        InEnum in = DetermineSourceUtil.determineSource(request, name);
        String detail = exception.getBody().getDetail();
        String invalidValue = null;
        return ProblemMediaType.INSTANCE
                .toResponse(new BadRequestProblem(InputValidationIssues.schemaViolation(in, name, invalidValue,
                        detail)));
    }

}
