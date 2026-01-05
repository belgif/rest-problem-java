package io.github.belgif.rest.problem.ee.jaxrs;

import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.belgif.rest.problem.InternalServerErrorProblem;
import io.github.belgif.rest.problem.api.Problem;

/**
 * Default ExceptionMapper for mapping unhandled exceptions to InternalServerErrorProblem.
 *
 * @see ExceptionMapper
 * @see InternalServerErrorProblem
 */
@Provider
public class DefaultExceptionMapper extends AbstractProblemExceptionMapper<Exception> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultExceptionMapper.class);

    public DefaultExceptionMapper() {
        super(Exception.class);
    }

    @Override
    public Problem toProblem(Exception exception) {
        LOGGER.error("Unhandled exception", exception);
        return new InternalServerErrorProblem();
    }

}
