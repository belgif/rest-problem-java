package io.github.belgif.rest.problem.ee.jaxrs;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import io.github.belgif.rest.problem.api.Problem;

/**
 * ExceptionMapper for {@link Problem}.
 *
 * @see ExceptionMapper
 */
public class ProblemExceptionMapper implements ExceptionMapper<Problem> {

    @Override
    public Response toResponse(Problem problem) {
        return ProblemMediaType.INSTANCE.toResponse(problem);
    }

}
