package io.github.belgif.rest.problem.ee.jaxrs;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import io.github.belgif.rest.problem.BadRequestProblem;

/**
 * Exception mapper for {@link javax.ws.rs.BadRequestException}, with special handling for unwrapping BadRequestProblem
 * cause thrown by {@link javax.ws.rs.ext.ParamConverter}.
 */
@Provider
public class BadRequestExceptionMapper extends AbstractUnwrappingExceptionMapper<BadRequestException> {

    @Override
    protected Response toDefaultResponse(BadRequestException exception) {
        return ProblemMediaType.INSTANCE.toResponse(new BadRequestProblem());
    }

}
