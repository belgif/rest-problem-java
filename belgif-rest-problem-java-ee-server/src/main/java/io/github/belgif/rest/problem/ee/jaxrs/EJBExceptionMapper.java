package io.github.belgif.rest.problem.ee.jaxrs;

import javax.ejb.EJBException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import io.github.belgif.rest.problem.api.Problem;

/**
 * ExceptionMapper for unhandled EJBExceptions.
 *
 * <p>
 * Routes to {@link ProblemExceptionMapper} when the EJBException is caused by a Problem exception.
 * This is a workaround to avoid having to annotate Problem with @ApplicationException.
 * Otherwise, routes to {@link DefaultExceptionMapper}.
 * </p>
 *
 * @see ExceptionMapper
 * @see EJBException
 * @see ProblemExceptionMapper
 * @see DefaultExceptionMapper
 */
@Provider
public class EJBExceptionMapper implements ExceptionMapper<EJBException> {

    private static final ProblemExceptionMapper PROBLEM_MAPPER = new ProblemExceptionMapper();

    private static final DefaultExceptionMapper DEFAULT_MAPPER = new DefaultExceptionMapper();

    @Override
    public Response toResponse(EJBException exception) {
        if (exception.getCause() instanceof Problem) {
            return PROBLEM_MAPPER.toResponse((Problem) exception.getCause());
        } else {
            return DEFAULT_MAPPER.toResponse(exception);
        }
    }

}
