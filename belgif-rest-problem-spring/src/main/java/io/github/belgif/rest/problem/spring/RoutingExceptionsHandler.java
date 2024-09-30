package io.github.belgif.rest.problem.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
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
@ConditionalOnWebApplication
public class RoutingExceptionsHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoutingExceptionsHandler.class);

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
        LOGGER.info("Transforming HttpMessageNotReadableException " +
                "to a BadRequestProblem with sanitized detail message", exception);
        return ProblemMediaType.INSTANCE
                .toResponse(new BadRequestProblem(InputValidationIssues.schemaViolation(InEnum.BODY, null, null,
                        getSanitizedProblemDetailMessage(exception))));
    }

    private String getSanitizedProblemDetailMessage(HttpMessageNotReadableException exception) {
        if (exception.getMessage() != null) {
            String message = exception.getMessage();
            if (message.startsWith("Required request body is missing")) {
                return "Required request body is missing";
            }
            if (message.startsWith("JSON parse error")) {
                return "JSON parse error";
            }
        }
        return null;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Void> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException exception) {
        ResponseEntity.BodyBuilder response = ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED.value());
        if (exception.getSupportedHttpMethods() != null && !exception.getSupportedHttpMethods().isEmpty()) {
            response.allow(exception.getSupportedHttpMethods().toArray(new HttpMethod[0]));
        }
        return response.build();
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<Void> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException exception) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE.value()).build();
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Void> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException exception) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()).build();
    }

}
