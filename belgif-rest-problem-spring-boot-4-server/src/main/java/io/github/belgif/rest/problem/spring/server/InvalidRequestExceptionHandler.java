package io.github.belgif.rest.problem.spring.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import tools.jackson.core.JsonPointer;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * RestController exception handler for InvalidRequestException thrown by Atlassian swagger-request-validator library.
 */
@RestControllerAdvice
@Order(1)
// @Order(1) to take precedence over io.github.belgif.rest.problem.spring.ProblemExceptionHandler
public class InvalidRequestExceptionHandler extends AbstractInvalidRequestExceptionHandler<JsonNode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvalidRequestExceptionHandler.class);

    private final ObjectMapper mapper;

    public InvalidRequestExceptionHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    protected String getBodyValue(String name, AtomicReference<JsonNode> requestBodyReference,
            HttpServletRequest request) {
        JsonNode requestBody = getRequestBody(requestBodyReference, request);
        if (requestBody == null) {
            return null;
        }
        return extractString(requestBody.at(JsonPointer.compile(name)));
    }

    private JsonNode getRequestBody(AtomicReference<JsonNode> requestBodyReference, HttpServletRequest request) {
        if (requestBodyReference.get() == null) {
            if (request == null) {
                LOGGER.error("Cannot read requestBody because HttpRequest is null");
            } else {
                try {
                    InputStream inputStream = request.getInputStream();
                    requestBodyReference.set(mapper.readTree(inputStream));
                } catch (IOException ex) {
                    LOGGER.error("Error reading input stream", ex);
                }
            }
        }
        return requestBodyReference.get();
    }

    private String extractString(JsonNode valueNode) {
        if (valueNode.isMissingNode()) {
            return null;
        }
        String value;
        if (valueNode.isString()) {
            value = valueNode.asString();
            return value.isEmpty() ? null : value;
        }
        value = mapper.writeValueAsString(valueNode);
        return value.isEmpty() ? null : value;
    }

}
