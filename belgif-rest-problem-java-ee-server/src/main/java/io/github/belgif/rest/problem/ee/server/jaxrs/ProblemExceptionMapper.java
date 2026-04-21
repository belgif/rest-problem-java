package io.github.belgif.rest.problem.ee.server.jaxrs;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.ee.core.jaxrs.ProblemMediaType;

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
