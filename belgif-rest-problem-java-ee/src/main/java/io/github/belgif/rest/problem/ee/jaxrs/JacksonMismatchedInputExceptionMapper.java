package io.github.belgif.rest.problem.ee.jaxrs;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.internal.JacksonUtil;

/**
 * ExceptionMapper for mapping jackson MismatchedInputException to BadRequestProblem.
 *
 * @see MismatchedInputException
 * @see BadRequestProblem
 */
@Provider
public class JacksonMismatchedInputExceptionMapper implements ExceptionMapper<MismatchedInputException> {

    private static final DefaultExceptionMapper DEFAULT_MAPPER = new DefaultExceptionMapper();

    @Override
    public Response toResponse(MismatchedInputException exception) {
        try {
            return ProblemMediaType.INSTANCE.toResponse(JacksonUtil.toBadRequestProblem(exception));
        } catch (RuntimeException e) {
            return DEFAULT_MAPPER.toResponse(e);
        }
    }

}
