package io.github.belgif.rest.problem.ee.jaxrs;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.JsonMappingException;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.internal.JacksonUtil;

/**
 * ExceptionMapper for mapping jackson JsonMappingException to BadRequestProblem.
 *
 * @see JsonMappingException
 * @see BadRequestProblem
 */
@Provider
public class JacksonJsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {

    private static final DefaultExceptionMapper DEFAULT_MAPPER = new DefaultExceptionMapper();

    @Override
    public Response toResponse(JsonMappingException exception) {
        try {
            return ProblemMediaType.INSTANCE.toResponse(JacksonUtil.toBadRequestProblem(exception));
        } catch (RuntimeException e) {
            return DEFAULT_MAPPER.toResponse(e);
        }
    }

}
