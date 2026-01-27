package io.github.belgif.rest.problem.ee.jaxrs;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import io.github.belgif.rest.problem.ResourceNotFoundProblem;

/**
 * Exception mapper for {@link javax.ws.rs.NotFoundException}, with special handling for unwrapping BadRequestProblem
 * cause thrown by {@link javax.ws.rs.ext.ParamConverter}.
 */
public class NotFoundExceptionMapper extends AbstractUnwrappingExceptionMapper<NotFoundException> {

    @Override
    protected Response toDefaultResponse(NotFoundException exception) {
        return ProblemMediaType.INSTANCE.toResponse(new ResourceNotFoundProblem());
    }

}
