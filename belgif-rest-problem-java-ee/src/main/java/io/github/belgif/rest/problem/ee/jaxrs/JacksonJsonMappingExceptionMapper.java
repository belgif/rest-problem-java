package io.github.belgif.rest.problem.ee.jaxrs;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.fasterxml.jackson.databind.JsonMappingException;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.internal.Jackson2Util;

/**
 * ExceptionMapper for mapping jackson JsonMappingException to BadRequestProblem.
 *
 * @see JsonMappingException
 * @see BadRequestProblem
 */
public class JacksonJsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {

    private static final DefaultExceptionMapper DEFAULT_MAPPER = new DefaultExceptionMapper();

    @Override
    public Response toResponse(JsonMappingException exception) {
        try {
            return ProblemMediaType.INSTANCE.toResponse(Jackson2Util.toBadRequestProblem(exception));
        } catch (RuntimeException e) {
            return DEFAULT_MAPPER.toResponse(e);
        }
    }

}
