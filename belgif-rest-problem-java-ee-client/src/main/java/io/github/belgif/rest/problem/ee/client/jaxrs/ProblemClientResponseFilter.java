package io.github.belgif.rest.problem.ee.client.jaxrs;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.belgif.rest.problem.DefaultProblem;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.ee.core.jaxrs.JaxRsUtil;
import io.github.belgif.rest.problem.ee.core.jaxrs.ProblemMediaType;
import io.github.belgif.rest.problem.ee.core.jaxrs.ProblemObjectMapper;

/**
 * JAX-RS ClientResponseFilter that converts problem response to a ProblemWrapper exception.
 *
 * @see ClientResponseFilter
 * @see ProblemWrapper
 */
public class ProblemClientResponseFilter implements ClientResponseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProblemClientResponseFilter.class);

    @Inject
    private Instance<ObjectMapper> cdiObjectMapper;

    @Context
    private Providers providers;

    private volatile ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        if (this.objectMapper == null) {
            this.objectMapper = JaxRsUtil.locateObjectMapper(
                    providers, cdiObjectMapper, Problem.class,
                    MediaType.APPLICATION_JSON_TYPE, () -> ProblemObjectMapper.INSTANCE);
        }
    }

    @Override
    public void filter(ClientRequestContext request, ClientResponseContext response) throws IOException {
        if (request.getProperty("org.eclipse.microprofile.rest.client.invokedMethod") != null) {
            // Use io.github.belgif.rest.problem.jaxrs.client.ProblemResponseExceptionMapper on MicroProfile REST Client
            return;
        }
        init(); // because not all JAX-RS implementations honor the @PostConstruct
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
