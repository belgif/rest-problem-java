package io.github.belgif.rest.problem.spring;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
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
 * Order(1) to make sure it is passed through this class before ProblemExceptionHandler is invoked.
 */
@RestControllerAdvice
@ConditionalOnWebApplication
@Order(1)
@ConditionalOnClass(InvalidRequestException.class)
public class OpenApiRequestViolationProblemAdvice {
    private static final Logger log = LoggerFactory.getLogger(OpenApiRequestViolationProblemAdvice.class);

    private final ObjectMapper mapper;

    public OpenApiRequestViolationProblemAdvice(ObjectMapper mapper) {
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
            issue = getIssueWithCorrectType(message);
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
                return InputValidationIssues.schemaViolation(InEnum.BODY, null, null, "Unable to parse JSON");
            }
            case "validation.schema.unknownError": {
                log.error("An unknown error occured during schema validation: {}", message.getMessage());
                return InputValidationIssues.schemaViolation(null, null, null,
                        "An error occurred during schema validation");
            }
            default:
                return null;
        }
    }

    private InputValidationIssue getIssueWithCorrectType(ValidationReport.Message message) {
        switch (message.getKey()) {
            case "validation.request.parameter.query.unexpected":
                return InputValidationIssues.unknownInput(null, null, null);
            default:
                return InputValidationIssues.schemaViolation(null, null, null, null);
        }
    }

    private boolean isNonExistingPath(InvalidRequestException ex) {
        return ex.getValidationReport().getMessages().stream()
                .anyMatch(m -> "validation.request.path.missing".equals(m.getKey()));
    }

}
