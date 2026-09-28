package io.github.belgif.rest.problem.ee.client.jaxrs;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Providers;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.belgif.rest.problem.DefaultProblem;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.ee.core.jaxrs.JaxRsUtil;
import io.github.belgif.rest.problem.ee.core.jaxrs.ProblemMediaType;
import io.github.belgif.rest.problem.ee.core.jaxrs.ProblemObjectMapper;
import io.github.belgif.rest.problem.internal.Jackson2Util;

/**
 * Client-side problem mapper for MicroProfile REST Client.
 *
 * @see ResponseExceptionMapper
 * @see Problem
 */
public class ProblemResponseExceptionMapper implements ResponseExceptionMapper<Exception> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProblemResponseExceptionMapper.class);

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
    public Exception toThrowable(Response response) {
        init(); // because not all JAX-RS implementations honor the @PostConstruct
        if (ProblemMediaType.INSTANCE.isCompatible(response.getMediaType()) || (response.getStatus() >= 400
                && MediaType.APPLICATION_JSON_TYPE.isCompatible(response.getMediaType()))) {
            // buffer the entity so it can still be consumed downstream
            response.bufferEntity();
            JsonNode json = response.readEntity(JsonNode.class);
            if (response.getStatus() == 503 && Jackson2Util.isHealthDownResponse(json)) {
                // We observed issues with some MicroProfile runtimes handling the health DOWN response, so rather
                // than returning null and letting the runtime handle it, we directly throw WebApplicationException.
                return new WebApplicationException(response);
            }
            try {
                Problem problem = objectMapper.treeToValue(json, Problem.class);
                if (problem instanceof DefaultProblem) {
                    LOGGER.info("No @ProblemType registered for {}: using DefaultProblem fallback", problem.getType());
                }
                Jackson2Util.checkStatusCodeConsistency(response.getStatus(), json, problem);
                return problem;
            } catch (Exception e) {
                LOGGER.error("Problem reading problem type", e);
            }
        }
        return null;
    }

}
