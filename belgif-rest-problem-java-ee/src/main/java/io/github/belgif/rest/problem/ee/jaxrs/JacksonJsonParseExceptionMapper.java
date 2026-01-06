package io.github.belgif.rest.problem.ee.jaxrs;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonParseException;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.internal.JacksonUtil;

/**
 * ExceptionMapper for mapping jackson JsonParseException to BadRequestProblem.
 *
 * @see JsonParseException
 * @see BadRequestProblem
 */
@Provider
public class JacksonJsonParseExceptionMapper implements ExceptionMapper<JsonParseException> {

    @Override
    public Response toResponse(JsonParseException exception) {
        return ProblemMediaType.INSTANCE.toResponse(JacksonUtil.toBadRequestProblem(exception));
    }

}
