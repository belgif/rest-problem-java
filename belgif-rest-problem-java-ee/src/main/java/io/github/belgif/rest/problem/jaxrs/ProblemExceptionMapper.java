package io.github.belgif.rest.problem.jaxrs;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import io.github.belgif.rest.problem.api.Problem;

/**
 * ExceptionMapper for {@link Problem}.
 *
 * @see ExceptionMapper
 */
@Provider
public class ProblemExceptionMapper implements ExceptionMapper<Problem> {

    @Override
    public Response toResponse(Problem problem) {
        return ProblemMediaType.INSTANCE.toResponse(problem);
    }

}
