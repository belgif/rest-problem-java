/*
 * Copyright (c) Smals
 */
package io.github.belgif.rest.problem.ee.jaxrs;

import static io.github.belgif.rest.problem.api.InputValidationIssues.invalidStructure;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.io.JsonEOFException;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.config.ProblemConfig;

@Provider
public class JsonParseExceptionMapper implements ExceptionMapper<JsonParseException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonParseExceptionMapper.class);

    @Override
    public Response toResponse(JsonParseException exception) {
        LOGGER.warn(exception.getMessage(), exception);
        try {
            ProblemConfig.setExtIssueTypesEnabled(true);
            return Response.status(400).entity(new BadRequestProblem(invalidStructure(InEnum.BODY, "$.",
                    exception.getRequestPayloadAsString(), getReason(exception.getClass())))).build();
        } finally {
            ProblemConfig.setExtIssueTypesEnabled(false);
        }
    }

    private String getReason(Class<? extends JsonParseException> clazz) {
        if (clazz == JsonEOFException.class) {
            return "invalid json data (end-of-input reached unexpectedly)";
        } else {
            return "invalid json data";
        }
    }

}
