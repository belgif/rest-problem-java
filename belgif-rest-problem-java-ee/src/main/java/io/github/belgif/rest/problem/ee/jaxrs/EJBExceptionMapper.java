package io.github.belgif.rest.problem.ee.jaxrs;

import javax.ejb.EJBException;
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
public class EJBExceptionMapper extends AbstractProblemExceptionMapper<EJBException> {

    public EJBExceptionMapper() {
        super(EJBException.class);
    }

    @Override
    public Problem toProblem(EJBException exception) {
        if (exception.getCause() instanceof Problem) {
            return (Problem) exception.getCause();
        } else {
            return null;
        }
    }

}
