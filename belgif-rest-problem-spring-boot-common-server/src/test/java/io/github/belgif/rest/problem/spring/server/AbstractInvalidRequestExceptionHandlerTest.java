package io.github.belgif.rest.problem.spring.server;

import static org.assertj.core.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.atlassian.oai.validator.model.ApiOperation;
import com.atlassian.oai.validator.model.ApiPath;
import com.atlassian.oai.validator.model.ApiPathImpl;
import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.springmvc.InvalidRequestException;
import com.fasterxml.jackson.databind.JsonNode;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.ResourceNotFoundProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssues;
import io.github.belgif.rest.problem.api.Problem;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.parameters.Parameter;

class AbstractInvalidRequestExceptionHandlerTest {

    AbstractInvalidRequestExceptionHandler<JsonNode> handler = new AbstractInvalidRequestExceptionHandler<>() {
        @Override
        protected String getBodyValue(String name, AtomicReference<JsonNode> requestBody, HttpServletRequest request) {
            return "myDummyValue";
        }
    };

    @Test
    void inBodyTest() {
        ValidationReport.Message.Builder builder =
                ValidationReport.Message.create("something.crazy.going.on", "My Test Message");
        builder.withContext(ValidationReport.MessageContext.create()
                .withPointers("/myProperty", "yes")
                .build());

        MockHttpServletRequest request = new MockHttpServletRequest();
        String requestBody = "{\"myProperty\": \"myDummyValue\"}";
        request.setContent(requestBody.getBytes(StandardCharsets.UTF_8));
        request.setContentType("application/json");

        ValidationReport.Message message = builder.build();

        InvalidRequestException exception = new InvalidRequestException(ValidationReport.from(message));

        Problem problem = handler.handleInvalidRequestException(exception, request).getBody();

        assertThat(problem).isInstanceOf(BadRequestProblem.class);
        BadRequestProblem badRequestProblem = (BadRequestProblem) problem;
        assertThat(badRequestProblem.getIssues().get(0).getIn()).isEqualTo(InEnum.BODY);
        assertThat(badRequestProblem.getIssues().get(0).getName()).isEqualTo("/myProperty");
        assertThat(badRequestProblem.getIssues().get(0).getValue()).isEqualTo("myDummyValue");
    }

    @Test
    void inPathTest() {
        ApiPath path = new ApiPathImpl("/myPath/{myParam}/myOtherThing", "https://myPrefix.io");

        ValidationReport.Message.Builder builder =
                ValidationReport.Message.create("something.crazy.going.on", "My Test Message");
        builder.withContext(ValidationReport.MessageContext.create()
                .withParameter(new Parameter().in("path").name("myParam"))
                .withApiOperation(new ApiOperation(path, path, PathItem.HttpMethod.GET, new Operation()))
                .withRequestPath("/myPath/myValue/myOtherThing").build());
        MockHttpServletRequest request = new MockHttpServletRequest();

        ValidationReport.Message message = builder.build();

        InvalidRequestException exception = new InvalidRequestException(ValidationReport.from(message));

        Problem problem = handler.handleInvalidRequestException(exception, request).getBody();

        assertThat(problem).isInstanceOf(BadRequestProblem.class);
        BadRequestProblem badRequestProblem = (BadRequestProblem) problem;
        assertThat(badRequestProblem.getIssues().get(0).getIn()).isEqualTo(InEnum.PATH);
        assertThat(badRequestProblem.getIssues().get(0).getValue()).isEqualTo("myValue");
    }

    @Test
    void inQueryTest() {
        ValidationReport.Message.Builder builder =
                ValidationReport.Message.create("something.crazy.going.on", "My Test Message");
        builder.withContext(ValidationReport.MessageContext.create()
                .withParameter(new Parameter().in("query").name("myParam")).build());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("myParam", "myValue");

        InvalidRequestException exception = new InvalidRequestException(ValidationReport.from(builder.build()));

        Problem problem = handler.handleInvalidRequestException(exception, request).getBody();

        assertThat(problem).isInstanceOf(BadRequestProblem.class);
        BadRequestProblem badRequestProblem = (BadRequestProblem) problem;
        assertThat(badRequestProblem.getIssues().get(0).getIn()).isEqualTo(InEnum.QUERY);
        assertThat(badRequestProblem.getIssues().get(0).getValue()).isEqualTo("myValue");
        assertThat(badRequestProblem.getIssues().get(0).getName()).isEqualTo("myParam");
        assertThat(badRequestProblem.getIssues().get(0).getDetail()).isEqualTo("My Test Message");
    }

    @Test
    void inHeaderTest() {
        ValidationReport.Message.Builder builder =
                ValidationReport.Message.create("something.crazy.going.on", "My Test Message");
        builder.withContext(ValidationReport.MessageContext.create()
                .withParameter(new Parameter().in("header").name("myParam")).build());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("myParam", "myValue");

        InvalidRequestException exception = new InvalidRequestException(ValidationReport.from(builder.build()));

        Problem problem = handler.handleInvalidRequestException(exception, request).getBody();

        assertThat(problem).isInstanceOf(BadRequestProblem.class);
        BadRequestProblem badRequestProblem = (BadRequestProblem) problem;
        assertThat(badRequestProblem.getIssues().get(0).getIn()).isEqualTo(InEnum.HEADER);
        assertThat(badRequestProblem.getIssues().get(0).getValue()).isEqualTo("myValue");
        assertThat(badRequestProblem.getIssues().get(0).getName()).isEqualTo("myParam");
        assertThat(badRequestProblem.getIssues().get(0).getDetail()).isEqualTo("My Test Message");
    }

    @Test
    void missingPathResultsIn404Test() {
        List<ValidationReport.Message> messages = new ArrayList<>();
        messages.add(ValidationReport.Message.create("something.crazy.going.on", "My Test Message").build());
        messages.add(
                ValidationReport.Message.create("validation.request.path.missing", "Should result in a 404").build());

        InvalidRequestException exception = new InvalidRequestException(ValidationReport.from(messages));
        Problem problem = handler.handleInvalidRequestException(exception, null).getBody();
        assertThat(problem).isInstanceOf(ResourceNotFoundProblem.class);
        assertThat(problem.getStatus()).isEqualTo(404);
    }

    @Test
    void defaultMessagesResultIn400Test() {
        List<ValidationReport.Message> messages = new ArrayList<>();
        messages.add(ValidationReport.Message.create("something.crazy.going.on", "My Test Message").build());
        messages.add(ValidationReport.Message.create("Something else", "test message").build());

        InvalidRequestException exception = new InvalidRequestException(ValidationReport.from(messages));
        HttpServletRequest request = new MockHttpServletRequest();

        Problem problem = handler.handleInvalidRequestException(exception, request).getBody();
        assertThat(problem).isInstanceOf(BadRequestProblem.class);
        assertThat(problem.getStatus()).isEqualTo(400);
    }

    @Test
    void invalidJsonTest() {
        List<ValidationReport.Message> messages = new ArrayList<>();
        messages.add(ValidationReport.Message.create("validation.schema.invalidJson", "test message").build());

        InvalidRequestException exception = new InvalidRequestException(ValidationReport.from(messages));
        HttpServletRequest request = new MockHttpServletRequest();

        Problem problem = handler.handleInvalidRequestException(exception, request).getBody();
        assertThat(problem).isInstanceOf(BadRequestProblem.class);
        BadRequestProblem badRequestProblem = (BadRequestProblem) problem;
        assertThat(badRequestProblem.getIssues().get(0).getIn()).isEqualTo(InEnum.BODY);
        assertThat(badRequestProblem.getIssues().get(0).getDetail()).isEqualTo("Unable to parse JSON");
    }

    @Test
    void invalidJsonBodyTest() {
        List<ValidationReport.Message> messages = new ArrayList<>();
        messages.add(
                ValidationReport.Message.create("validation.request.body.schema.invalidJson", "test message").build());

        InvalidRequestException exception = new InvalidRequestException(ValidationReport.from(messages));
        HttpServletRequest request = new MockHttpServletRequest();

        Problem problem = handler.handleInvalidRequestException(exception, request).getBody();
        assertThat(problem).isInstanceOf(BadRequestProblem.class);
        BadRequestProblem badRequestProblem = (BadRequestProblem) problem;
        assertThat(badRequestProblem.getIssues().get(0).getIn()).isEqualTo(InEnum.BODY);
        assertThat(badRequestProblem.getIssues().get(0).getDetail()).isEqualTo("Unable to parse JSON");
    }

    @Test
    void unknownErrorTest() {
        List<ValidationReport.Message> messages = new ArrayList<>();
        messages.add(ValidationReport.Message.create("validation.schema.unknownError", "test message").build());

        InvalidRequestException exception = new InvalidRequestException(ValidationReport.from(messages));
        HttpServletRequest request = new MockHttpServletRequest();

        Problem problem = handler.handleInvalidRequestException(exception, request).getBody();
        assertThat(problem).isInstanceOf(BadRequestProblem.class);
        BadRequestProblem badRequestProblem = (BadRequestProblem) problem;
        assertThat(badRequestProblem.getIssues().get(0).getDetail())
                .isEqualTo("An error occurred during schema validation");
    }

    @Test
    void issuesAreExplodedForAllOfTest() {
        List<ValidationReport.Message> messages = new ArrayList<>();
        messages.add(ValidationReport.Message.create("validation.schema.firstError", "first issue").build());
        messages.add(ValidationReport.Message.create("validation.schema.secondError", "second issue").build());

        ValidationReport.Message message = ValidationReport.Message
                .create("validation.request.body.schema.allOf", "test message").build().withNestedMessages(messages);

        InvalidRequestException exception = new InvalidRequestException(ValidationReport.from(message));
        HttpServletRequest request = new MockHttpServletRequest();

        Problem problem = handler.handleInvalidRequestException(exception, request).getBody();
        assertThat(problem).isInstanceOf(BadRequestProblem.class);
        BadRequestProblem badRequestProblem = (BadRequestProblem) problem;
        assertThat(badRequestProblem.getIssues()).hasSize(2);
    }

    @Test
    void issuesAreNotExplodedForOtherIssuesThanAllOfTest() {
        List<ValidationReport.Message> messages = new ArrayList<>();
        messages.add(ValidationReport.Message.create("validation.schema.firstError", "first issue").build());
        messages.add(ValidationReport.Message.create("validation.schema.secondError", "second issue").build());

        ValidationReport.Message message = ValidationReport.Message.create("my.validation.issue", "test message")
                .build().withNestedMessages(messages);

        InvalidRequestException exception = new InvalidRequestException(ValidationReport.from(message));
        HttpServletRequest request = new MockHttpServletRequest();

        Problem problem = handler.handleInvalidRequestException(exception, request).getBody();
        assertThat(problem).isInstanceOf(BadRequestProblem.class);
        BadRequestProblem badRequestProblem = (BadRequestProblem) problem;
        assertThat(badRequestProblem.getIssues()).hasSize(1);
    }

    @Test
    void defaultIssuesAreOfTypeSchemaViolationTest() {
        List<ValidationReport.Message> messages = new ArrayList<>();
        messages.add(ValidationReport.Message.create("my.violation", "test message").build());

        InvalidRequestException exception = new InvalidRequestException(ValidationReport.from(messages));
        HttpServletRequest request = new MockHttpServletRequest();

        Problem problem = handler.handleInvalidRequestException(exception, request).getBody();
        assertThat(problem).isInstanceOf(BadRequestProblem.class);
        BadRequestProblem badRequestProblem = (BadRequestProblem) problem;
        assertThat(badRequestProblem.getIssues().get(0).getType())
                .isEqualTo(InputValidationIssues.ISSUE_TYPE_SCHEMA_VIOLATION);
    }

    @Test
    void missingQueryParamIssuesAreOfTypeUnknownInputTest() {
        List<ValidationReport.Message> messages = new ArrayList<>();
        messages.add(ValidationReport.Message.create("validation.request.parameter.query.unexpected", "test message")
                .build());

        InvalidRequestException exception = new InvalidRequestException(ValidationReport.from(messages));
        HttpServletRequest request = new MockHttpServletRequest();

        Problem problem = handler.handleInvalidRequestException(exception, request).getBody();
        assertThat(problem).isInstanceOf(BadRequestProblem.class);
        BadRequestProblem badRequestProblem = (BadRequestProblem) problem;
        assertThat(badRequestProblem.getIssues().get(0).getType())
                .isEqualTo(InputValidationIssues.ISSUE_TYPE_UNKNOWN_INPUT);
    }

}
