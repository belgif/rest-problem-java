package io.github.belgif.rest.problem.jaxrs.client;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.belgif.rest.problem.DefaultProblem;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.jaxrs.ProblemMediaType;

/**
 * Client-side problem mapper for MicroProfile REST Client.
 *
 * @see ResponseExceptionMapper
 * @see Problem
 */
public class ProblemResponseExceptionMapper implements ResponseExceptionMapper<Problem> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProblemResponseExceptionMapper.class);

    @Override
    public Problem toThrowable(Response response) {
        if (ProblemMediaType.INSTANCE.isCompatible(response.getMediaType()) || (response.getStatus() >= 400
                && MediaType.APPLICATION_JSON_TYPE.isCompatible(response.getMediaType()))) {
            Problem problem = response.readEntity(Problem.class);
            if (problem instanceof DefaultProblem) {
                LOGGER.info("No @ProblemType registered for {}: using DefaultProblem fallback", problem.getType());
            }
            return problem;
        }
        return null;
    }

}
