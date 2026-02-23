package io.github.belgif.rest.problem.spring.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        JsonNode valueNode = requestBody.at(JsonPointer.compile(name));
        return valueNode.asText().isEmpty() ? null : valueNode.asText();
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

}
