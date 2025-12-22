package io.github.belgif.rest.problem.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.belgif.rest.problem.InternalServerErrorProblem;
import io.github.belgif.rest.problem.api.Problem;

/**
 * Exception handler for RestControllers.
 *
 * <p>
 * Handles the response serialization for Problem exceptions, and converts all other uncaught exceptions
 * to an InternalServerErrorProblem.
 * </p>
 */
@RestControllerAdvice
@ConditionalOnWebApplication
public class ProblemExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProblemExceptionHandler.class);

    @ExceptionHandler(Problem.class)
    public ResponseEntity<Problem> handleProblem(Problem problem) {
        return ProblemMediaType.INSTANCE.toResponse(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Problem> handleException(Exception exception) {
        LOGGER.error("Unhandled exception", exception);
        return ProblemMediaType.INSTANCE.toResponse(new InternalServerErrorProblem());
    }

}
