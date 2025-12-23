package io.github.belgif.rest.problem.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.internal.Jackson2Util;

/**
 * RestController exception handler for routing-related exceptions.
 */
@RestControllerAdvice
@ConditionalOnWebApplication
@Order(1)
// @Order(1) to take precedence over org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver
// and io.github.belgif.rest.problem.spring.ProblemExceptionHandler
public class RoutingExceptionsHandler extends AbstractRoutingExceptionsHandler<MismatchedInputException> {

    public RoutingExceptionsHandler() {
        super(MismatchedInputException.class);
    }

    @Override
    protected BadRequestProblem toBadRequestProblem(MismatchedInputException mismatchedInputException) {
        return Jackson2Util.toBadRequestProblem(mismatchedInputException);
    }

}
