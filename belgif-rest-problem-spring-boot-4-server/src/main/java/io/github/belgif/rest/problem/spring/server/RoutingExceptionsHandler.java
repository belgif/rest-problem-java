package io.github.belgif.rest.problem.spring.server;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.internal.Jackson3Util;
import tools.jackson.core.JacksonException;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.DatabindException;

/**
 * RestController exception handler for routing-related exceptions.
 */
@RestControllerAdvice
@Order(1)
// @Order(1) to take precedence over org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver
// and io.github.belgif.rest.problem.spring.ProblemExceptionHandler
//TODO: move to reinstated spring(-7) module?
public class RoutingExceptionsHandler extends AbstractRoutingExceptionsHandler<JacksonException> {

    public RoutingExceptionsHandler() {
        super(JacksonException.class);
    }

    @Override
    protected BadRequestProblem toBadRequestProblem(JacksonException jacksonException) {
        if (jacksonException instanceof DatabindException databindException) {
            return Jackson3Util.toBadRequestProblem(databindException);
        } else if (jacksonException instanceof StreamReadException streamReadException) {
            return Jackson3Util.toBadRequestProblem(streamReadException);
        } else {
            return null;
        }
    }

}
