package io.github.belgif.rest.problem.ee.jaxrs;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import io.github.belgif.rest.problem.BadRequestProblem;

/**
 * ExceptionMapper for mapping jackson MismatchedInputException to BadRequestProblem.
 *
 * @see MismatchedInputException
 * @see BadRequestProblem
 */
// NOTE: This specific specialization for MismatchedInputException is needed because some runtimes (i.e. Quarkus)
// provide a default ExceptionMapper for MismatchedInputException, which we need to override
public class JacksonMismatchedInputExceptionMapper implements ExceptionMapper<MismatchedInputException> {

    private static final JacksonJsonMappingExceptionMapper DELEGATE = new JacksonJsonMappingExceptionMapper();

    @Override
    public Response toResponse(MismatchedInputException exception) {
        return DELEGATE.toResponse(exception);
    }

}
