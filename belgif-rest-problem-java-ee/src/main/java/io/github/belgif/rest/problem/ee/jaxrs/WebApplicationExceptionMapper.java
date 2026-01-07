package io.github.belgif.rest.problem.ee.jaxrs;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

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

    private static final JacksonJsonParseExceptionMapper JSON_PARSE_EXCEPTION_MAPPER =
            new JacksonJsonParseExceptionMapper();

    private static final JacksonJsonMappingExceptionMapper JSON_MAPPING_EXCEPTION_MAPPER =
            new JacksonJsonMappingExceptionMapper();

    @Override
    public Response toResponse(WebApplicationException exception) {
        // On some JAX-RS runtimes (i.e. Quarkus), JsonParseException and JsonMappingException
        // are wrapped in a WebApplicationException, which gets unwrapped here
        if (exception.getCause() instanceof JsonParseException) {
            return JSON_PARSE_EXCEPTION_MAPPER.toResponse((JsonParseException) exception.getCause());
        } else if (exception.getCause() instanceof JsonMappingException) {
            return JSON_MAPPING_EXCEPTION_MAPPER.toResponse((JsonMappingException) exception.getCause());
        }
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
