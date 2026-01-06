package io.github.belgif.rest.problem.spring;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.springmvc.InvalidRequestException;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.ResourceNotFoundProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.spring.internal.InvalidRequestExceptionUtil;

/**
 * RestController exception handler for InvalidRequestException thrown by Atlassian swagger-request-validator library.
 *
 * @param <J> the type of the Jackson JsonNode
 */
public abstract class AbstractInvalidRequestExceptionHandler<J> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractInvalidRequestExceptionHandler.class);

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
        AtomicReference<J> requestBody = new AtomicReference<>(null);
        for (ValidationReport.Message message : getExplodedMessages(ex)) {
            InputValidationIssue issue = handleSpecialCases(message);
            if (issue != null) {
                issues.add(issue);
                continue;
            }
            InEnum in = InvalidRequestExceptionUtil.getIn(message);
            String name = InvalidRequestExceptionUtil.getName(message);
            String value = getValue(message, in, name, request, requestBody);
            String detail = InvalidRequestExceptionUtil.getDetail(message);

            issues.add(buildIssue(message, in, name, value, detail));
        }
        return new ArrayList<>(issues);
    }

    private String getValue(ValidationReport.Message message, InEnum in, String name, HttpServletRequest request,
            AtomicReference<J> requestBody) {
        return switch (in) {
            case PATH -> InvalidRequestExceptionUtil.getPathValue(message, name);
            case QUERY -> request.getParameter(name);
            case HEADER -> request.getHeader(name);
            case BODY -> getBodyValue(name, requestBody, request);
        };
    }

    protected abstract String getBodyValue(String name, AtomicReference<J> requestBodyReference,
            HttpServletRequest request);

    private InputValidationIssue handleSpecialCases(ValidationReport.Message message) {
        return switch (message.getKey()) {
            case "validation.schema.invalidJson", "validation.request.body.schema.invalidJson" ->
                InputValidationIssues.schemaViolation(InEnum.BODY, null, null, "Unable to parse JSON");
            case "validation.schema.unknownError" -> {
                LOGGER.error("An unknown error occurred during schema validation: {}", message.getMessage());
                yield InputValidationIssues.schemaViolation(null, null, null,
                        "An error occurred during schema validation");
            }
            default -> null;
        };
    }

    private InputValidationIssue buildIssue(ValidationReport.Message message, InEnum in, String name,
            String value, String detail) {
        if (message.getKey().equals("validation.request.parameter.query.unexpected")) {
            return InputValidationIssues.unknownInput(in, name, value);
        }
        return InputValidationIssues.schemaViolation(in, name, value, detail);
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

}
