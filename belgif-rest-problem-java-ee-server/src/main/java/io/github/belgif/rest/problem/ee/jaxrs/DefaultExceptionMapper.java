package io.github.belgif.rest.problem.ee.jaxrs;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.belgif.rest.problem.InternalServerErrorProblem;

/**
 * Default ExceptionMapper for mapping unhandled exceptions to InternalServerErrorProblem.
 *
 * @see ExceptionMapper
 * @see InternalServerErrorProblem
 */
@Provider
public class DefaultExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultExceptionMapper.class);

    @Override
    public Response toResponse(Exception exception) {
        LOGGER.error("Unhandled exception", exception);
        return ProblemMediaType.INSTANCE.toResponse(new InternalServerErrorProblem());
    }

}
