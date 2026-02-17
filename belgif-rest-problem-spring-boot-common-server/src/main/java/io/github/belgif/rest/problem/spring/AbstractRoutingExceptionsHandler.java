package io.github.belgif.rest.problem.spring;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssues;
import io.github.belgif.rest.problem.api.Problem;

/**
 * RestController exception handler for routing-related exceptions.
 *
 * @param <T> the type of the Jackson exception
 */
public abstract class AbstractRoutingExceptionsHandler<T extends Exception> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRoutingExceptionsHandler.class);

    private static final ProblemExceptionHandler DEFAULT_PROBLEM_EXCEPTION_HANDLER = new ProblemExceptionHandler();

    private final Class<T> jacksonExceptionClass;

    protected AbstractRoutingExceptionsHandler(Class<T> jacksonExceptionClass) {
        this.jacksonExceptionClass = jacksonExceptionClass;
    }

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
    @SuppressWarnings("unchecked")
    public ResponseEntity<Problem> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        if (exception.getCause() != null && jacksonExceptionClass.isAssignableFrom(exception.getCause().getClass())) {
            T jacksonException = (T) exception.getCause();
            if (Arrays.stream(jacksonException.getStackTrace())
                    .anyMatch(e -> e.getClassName().startsWith("org.springframework.web.client"))) {
                // When the MismatchedInputException originates from a REST Client API, it relates to an
                // invalid inbound response and should be mapped to HTTP 500 Internal Server Error
                return DEFAULT_PROBLEM_EXCEPTION_HANDLER.handleException(exception);
            }
            BadRequestProblem problem = toBadRequestProblem(jacksonException);
            if (problem != null) {
                return ProblemMediaType.INSTANCE.toResponse(problem);
            }
        }
        LOGGER.info("Transforming HttpMessageNotReadableException "
                + "to a BadRequestProblem with sanitized detail message", exception);
        return ProblemMediaType.INSTANCE
                .toResponse(new BadRequestProblem(InputValidationIssues.schemaViolation(InEnum.BODY, null, null,
                        getSanitizedProblemDetailMessage(exception))));
    }

    protected abstract BadRequestProblem toBadRequestProblem(T jacksonException);

    private String getSanitizedProblemDetailMessage(HttpMessageNotReadableException exception) {
        if (exception.getMessage() != null) {
            String message = exception.getMessage();
            if (message.startsWith("Required request body is missing")) {
                return "Required request body is missing";
            }
            if (message.startsWith("JSON parse error")) {
                return InputValidationIssues.DETAIL_JSON_SYNTAX_ERROR;
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
