/*
 * Copyright (c) Smals
 */
package io.github.belgif.rest.problem.ee.jaxrs;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;

import io.github.belgif.rest.problem.internal.JacksonUtil;

@Provider
public class JsonParseExceptionMapper implements ExceptionMapper<JsonParseException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonParseExceptionMapper.class);

    @Override
    public Response toResponse(JsonParseException exception) {
        LOGGER.warn(exception.getMessage(), exception);
        return ProblemMediaType.INSTANCE.toResponse(JacksonUtil.toBadRequestProblem(exception));
    }

}
