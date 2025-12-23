package io.github.belgif.rest.problem.spring;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.atlassian.oai.validator.springmvc.InvalidRequestException;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * RestController exception handler for InvalidRequestException thrown by Atlassian swagger-request-validator library.
 */
@RestControllerAdvice
@ConditionalOnWebApplication
@ConditionalOnClass(InvalidRequestException.class)
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
        JsonNode requestBody = getRequestBody(requestBodyReference, request, mapper);
        if (requestBody == null) {
            return null;
        }
        JsonNode valueNode = requestBody.at(JsonPointer.compile(name));
        return valueNode.asText().isEmpty() ? null : valueNode.asText();
    }

    private static JsonNode getRequestBody(AtomicReference<JsonNode> requestBodyReference, HttpServletRequest request,
            ObjectMapper mapper) {
        if (requestBodyReference.get() == null) {
            try {
                InputStream inputStream = request.getInputStream();
                requestBodyReference.set(mapper.readTree(inputStream));
            } catch (IOException ex) {
                LOGGER.error("Error reading input stream", ex);
            } catch (NullPointerException ex) {
                LOGGER.error("Cannot read requestBody because HttpRequest is null");
            }
        }
        return requestBodyReference.get();
    }

}
