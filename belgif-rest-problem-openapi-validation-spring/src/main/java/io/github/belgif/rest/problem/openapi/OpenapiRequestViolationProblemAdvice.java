package io.github.belgif.rest.problem.openapi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
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
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.spring.ProblemMediaType;
import io.swagger.v3.oas.models.parameters.Parameter;

@RestControllerAdvice
@ConditionalOnWebApplication
public class OpenapiRequestViolationProblemAdvice {

    private static final URI SCHEMA_VIOLATION_URI =
            URI.create("urn:problem-type:belgif:input-validation:schemaViolation");
    private static final String ISSUE_TITLE = "Input value is invalid with respect to the schema.";
    private static final Logger log = LoggerFactory.getLogger(OpenapiRequestViolationProblemAdvice.class);

    private final ObjectMapper mapper;

    public OpenapiRequestViolationProblemAdvice(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @ExceptionHandler(value = InvalidRequestException.class)
    public ResponseEntity<Problem> handleInvalidRequestException(final InvalidRequestException ex,
            HttpServletRequest request) {
        if (isNonExistingPath(ex)) {
            return ProblemMediaType.INSTANCE.toResponse(new ResourceNotFoundProblem());
        }
        List<InputValidationIssue> issues = constructIssues(ex, request);
        return ProblemMediaType.INSTANCE.toResponse(new BadRequestProblem(issues));
    }

    private List<InputValidationIssue> constructIssues(InvalidRequestException ex, HttpServletRequest request) {
        Set<InputValidationIssue> issues = new HashSet<>();
        for (ValidationReport.Message message : ex.getValidationReport().getMessages()) {
            InputValidationIssue issue = handleSpecialCases(message);
            if (issue != null) {
                issues.add(issue);
                continue;
            }
            issue = getEmptyIssue();
            issue.setIn(getIn(message));
            issue.setName(getName(message));
            issue.setValue(getValue(message, issue.getIn(), issue.getName(), request));
            issue.setDetail(message.getMessage());

            issues.add(issue);
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

    private String getValue(ValidationReport.Message message, InEnum in, String name, HttpServletRequest request) {
        switch (in) {
            case PATH:
                return getPathValue(message, name);
            case QUERY:
                return request.getParameter(name);
            case HEADER:
                return request.getHeader(name);
            case BODY:
                return getBodyValue(name, request);
            default:
                return null;
        }
    }

    private String getBodyValue(String name, HttpServletRequest request) {
        try {
            InputStream inputStream = request.getInputStream();
            JsonNode node = mapper.readTree(inputStream);
            JsonNode valueNode = node.at(JsonPointer.compile(name));
            return valueNode.asText().isEmpty() ? null : valueNode.asText();
        } catch (IOException e) {
            log.error("Error reading input stream", e);
        }
        return null;
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
                InputValidationIssue issue = getEmptyIssue();
                issue.setDetail("Unable to parse JSON");
                issue.setIn(InEnum.BODY);
                return issue;
            }
            case "validation.schema.unknownError": {
                return unknownError(message);
            }
            default:
                return null;
        }
    }

    private InputValidationIssue unknownError(ValidationReport.Message message) {
        log.error("An unknown error occured during schema validation: {}", message.getMessage());
        InputValidationIssue issue = getEmptyIssue();
        issue.setDetail("An error occurred during schema validation");
        return issue;
    }

    private InputValidationIssue getEmptyIssue() {
        InputValidationIssue issue = new InputValidationIssue();
        issue.setType(SCHEMA_VIOLATION_URI);
        issue.setTitle(ISSUE_TITLE);
        return issue;
    }

    private boolean isNonExistingPath(InvalidRequestException ex) {
        return ex.getValidationReport().getMessages().stream()
                .anyMatch(m -> "validation.request.path.missing".equals(m.getKey()));
    }

}
