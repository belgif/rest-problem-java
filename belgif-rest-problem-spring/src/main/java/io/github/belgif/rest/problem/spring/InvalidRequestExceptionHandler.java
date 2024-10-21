package io.github.belgif.rest.problem.spring;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.springmvc.InvalidRequestException;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.ResourceNotFoundProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;
import io.github.belgif.rest.problem.api.Problem;
import io.swagger.v3.oas.models.parameters.Parameter;

/**
 * This class is only active when Spring-Boot applications implemented the Atlassian swagger-request-validator.
 * Maps the InvalidRequestException thrown by the swagger-request-validator to belgif problems.
 */
@RestControllerAdvice
@ConditionalOnWebApplication
@ConditionalOnClass(InvalidRequestException.class)
@Order(1)
// @Order(1) to take precedence over io.github.belgif.rest.problem.spring.ProblemExceptionHandler
public class InvalidRequestExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvalidRequestExceptionHandler.class);

    private final ObjectMapper mapper;

    public InvalidRequestExceptionHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @ExceptionHandler(value = InvalidRequestException.class)
    public ResponseEntity<Problem> handleInvalidRequestException(InvalidRequestException ex,
            HttpServletRequest request) {
        if (isNonExistingPath(ex)) {
            return ProblemMediaType.INSTANCE.toResponse(new ResourceNotFoundProblem());
        }
        List<InputValidationIssue> issues = constructIssues(ex, request);
        return ProblemMediaType.INSTANCE.toResponse(new BadRequestProblem(issues));
    }

    private List<InputValidationIssue> constructIssues(InvalidRequestException ex, HttpServletRequest request) {
        Set<InputValidationIssue> issues = new LinkedHashSet<>();
        JsonNode requestBody = getRequestBody(request);
        for (ValidationReport.Message message : getExplodedMessages(ex)) {
            InputValidationIssue issue = handleSpecialCases(message);
            if (issue != null) {
                issues.add(issue);
                continue;
            }
            InEnum in = getIn(message);
            String name = getName(message);
            String value = getValue(message, in, name, request, requestBody);
            String detail = getDetail(message);

            issues.add(buildIssue(message, in, name, value, detail));
        }
        return new ArrayList<>(issues);
    }

    private InEnum getIn(ValidationReport.Message message) {
        return message.getContext()
                .flatMap(ValidationReport.MessageContext::getParameter)
                .map(parameter -> InEnum.fromValue(parameter.getIn()))
                .orElse(InEnum.BODY);
    }

    private String getName(ValidationReport.Message message) {
        return message.getContext()
                .flatMap(ValidationReport.MessageContext::getParameter)
                .map(Parameter::getName)
                .orElseGet(() -> message.getContext()
                        .flatMap(ValidationReport.MessageContext::getPointers)
                        .map(ValidationReport.MessageContext.Pointers::getInstance)
                        .orElse(null));
    }

    private String getValue(ValidationReport.Message message, InEnum in, String name, HttpServletRequest request,
            JsonNode requestBody) {
        switch (in) {
            case PATH:
                return getPathValue(message, name);
            case QUERY:
                return request.getParameter(name);
            case HEADER:
                return request.getHeader(name);
            case BODY:
                return getBodyValue(name, requestBody);
            default:
                return null;
        }
    }

    private String getDetail(ValidationReport.Message message) {
        if ("validation.request.body.schema.oneOf".equals(message.getKey())
                || "validation.request.body.schema.anyOf".equals(message.getKey())) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(message.getMessage()).append(" : ");
            for (ValidationReport.Message nestedMessage : message.getNestedMessages()) {
                stringBuilder.append(" -- ").append(nestedMessage.getMessage());
            }
            return stringBuilder.toString();
        } else {
            return message.getMessage();
        }
    }

    private String getBodyValue(String name, JsonNode requestBody) {
        if (requestBody == null) {
            return null;
        }
        JsonNode valueNode = requestBody.at(JsonPointer.compile(name));
        return valueNode.asText().isEmpty() ? null : valueNode.asText();
    }

    private String getPathValue(ValidationReport.Message message, String name) {
        String original = message.getContext().flatMap(ValidationReport.MessageContext::getApiOperation)
                .map(p -> p.getApiPath().original()).orElse(null);
        if (original != null) {
            String actual = message.getContext()
                    .flatMap(ValidationReport.MessageContext::getRequestPath)
                    .orElse("");

            String regex = original.replace("{" + name + "}", "(.*)").replaceAll("/\\{.*}", "/.*");

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(actual);

            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        return null;
    }

    private InputValidationIssue handleSpecialCases(ValidationReport.Message message) {
        switch (message.getKey()) {
            case "validation.schema.invalidJson":
            case "validation.request.body.schema.invalidJson": {
                return InputValidationIssues.schemaViolation(InEnum.BODY, null, null, "Unable to parse JSON");
            }
            case "validation.schema.unknownError": {
                LOGGER.error("An unknown error occured during schema validation: {}", message.getMessage());
                return InputValidationIssues.schemaViolation(null, null, null,
                        "An error occurred during schema validation");
            }
            default:
                return null;
        }
    }

    private InputValidationIssue buildIssue(ValidationReport.Message message, InEnum in, String name,
            String value, String detail) {
        switch (message.getKey()) {
            case "validation.request.parameter.query.unexpected":
                return InputValidationIssues.unknownInput(in, name, value);
            default:
                return InputValidationIssues.schemaViolation(in, name, value, detail);
        }
    }

    private boolean isNonExistingPath(InvalidRequestException ex) {
        return ex.getValidationReport().getMessages().stream()
                .anyMatch(m -> "validation.request.path.missing".equals(m.getKey()));
    }

    private List<ValidationReport.Message> getExplodedMessages(InvalidRequestException ex) {
        List<ValidationReport.Message> messages = new ArrayList<>();
        for (ValidationReport.Message message : ex.getValidationReport().getMessages()) {
            if ("validation.request.body.schema.allOf".equals(message.getKey()) && message.getNestedMessages() != null
                    && !message.getNestedMessages().isEmpty()) {
                messages.addAll(message.getNestedMessages());
            } else {
                messages.add(message);
            }
        }
        return messages;
    }

    private JsonNode getRequestBody(HttpServletRequest request) {
        try {
            InputStream inputStream = request.getInputStream();
            return mapper.readTree(inputStream);
        } catch (IOException ex) {
            if (!"GET".equals(request.getMethod()) && !"HEAD".equals(request.getMethod())) {
                LOGGER.error("Error reading input stream", ex);
            }
        }
        return null;
    }

}
