package io.github.belgif.rest.problem.ee.jaxrs;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonParseException;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.internal.Jackson2Util;

/**
 * ExceptionMapper for mapping jackson JsonParseException to BadRequestProblem.
 *
 * @see JsonParseException
 * @see BadRequestProblem
 */
@Provider
public class JacksonJsonParseExceptionMapper implements ExceptionMapper<JsonParseException> {

    private static final DefaultExceptionMapper DEFAULT_MAPPER = new DefaultExceptionMapper();

    @Override
    public Response toResponse(JsonParseException exception) {
        try {
            return ProblemMediaType.INSTANCE.toResponse(Jackson2Util.toBadRequestProblem(exception));
        } catch (RuntimeException e) {
            return DEFAULT_MAPPER.toResponse(e);
        }
    }

}
