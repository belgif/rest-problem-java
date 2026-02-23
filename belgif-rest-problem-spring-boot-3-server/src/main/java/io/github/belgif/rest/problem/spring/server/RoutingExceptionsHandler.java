package io.github.belgif.rest.problem.spring.server;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.internal.Jackson2Util;

/**
 * RestController exception handler for routing-related exceptions.
 */
@RestControllerAdvice
@Order(1)
// @Order(1) to take precedence over org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver
// and io.github.belgif.rest.problem.spring.ProblemExceptionHandler
public class RoutingExceptionsHandler extends AbstractRoutingExceptionsHandler<JacksonException> {

    public RoutingExceptionsHandler() {
        super(JacksonException.class);
    }

    @Override
    protected BadRequestProblem toBadRequestProblem(JacksonException jacksonException) {
        if (jacksonException instanceof JsonMappingException jsonMappingException) {
            return Jackson2Util.toBadRequestProblem(jsonMappingException);
        } else if (jacksonException instanceof JsonParseException jsonParseException) {
            return Jackson2Util.toBadRequestProblem(jsonParseException);
        } else {
            return null;
        }
    }

}
