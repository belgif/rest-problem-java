package io.github.belgif.rest.problem.ee.jaxrs.client;

import java.io.IOException;
import java.util.Objects;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.belgif.rest.problem.DefaultProblem;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.ee.jaxrs.ProblemMediaType;
import io.github.belgif.rest.problem.ee.jaxrs.ProblemObjectMapper;

/**
 * JAX-RS ClientResponseFilter that converts problem response to a ProblemWrapper exception.
 *
 * @see ClientResponseFilter
 * @see ProblemWrapper
 */
@Provider
public class ProblemClientResponseFilter implements ClientResponseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProblemClientResponseFilter.class);

    private final ObjectMapper objectMapper;

    public ProblemClientResponseFilter() {
        this(ProblemObjectMapper.INSTANCE);
    }

    public ProblemClientResponseFilter(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "ObjectMapper should not be null");
    }

    @Override
    public void filter(ClientRequestContext request, ClientResponseContext response) throws IOException {
        if (request.getProperty("org.eclipse.microprofile.rest.client.invokedMethod") != null) {
            // Use io.github.belgif.rest.problem.jaxrs.client.ProblemResponseExceptionMapper on MicroProfile REST Client
            return;
        }
        if (ProblemMediaType.INSTANCE.isCompatible(response.getMediaType()) || (response.getStatus() >= 400
                && MediaType.APPLICATION_JSON_TYPE.isCompatible(response.getMediaType()))) {
            Problem problem = objectMapper.readValue(response.getEntityStream(), Problem.class);
            if (problem instanceof DefaultProblem) {
                LOGGER.info("No @ProblemType registered for {}: using DefaultProblem fallback", problem.getType());
            }
            throw new ProblemWrapper(problem);
        }
    }

}
