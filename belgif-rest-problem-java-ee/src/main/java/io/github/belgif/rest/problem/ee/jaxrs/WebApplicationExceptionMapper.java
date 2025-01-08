package io.github.belgif.rest.problem.ee.jaxrs;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ExceptionMapper for unhandled WebApplicationExceptions.
 *
 * <p>
 * In the absence of this ExceptionMapper, WebApplicationExceptions thrown by the JAX-RS server runtime would get
 * mapped to InternalServerErrorProblem by the {@link DefaultExceptionMapper}.
 * </p>
 *
 * @see ExceptionMapper
 * @see WebApplicationException
 */
@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebApplicationExceptionMapper.class);

    @Override
    public Response toResponse(WebApplicationException exception) {
        Response response = exception.getResponse();
        if (response.getStatusInfo().getFamily() == Status.Family.SERVER_ERROR) {
            LOGGER.error("WebApplicationException [HTTP {}] {}", response.getStatus(), exception.getMessage(),
                    exception);
        } else {
            LOGGER.debug("WebApplicationException [HTTP {}] {}", response.getStatus(), exception.getMessage());
        }
        return response;
    }

}
