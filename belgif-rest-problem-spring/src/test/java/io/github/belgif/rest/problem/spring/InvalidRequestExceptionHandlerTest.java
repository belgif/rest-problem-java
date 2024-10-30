package io.github.belgif.rest.problem.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.atlassian.oai.validator.model.ApiOperation;
import com.atlassian.oai.validator.model.ApiPath;
import com.atlassian.oai.validator.model.ApiPathImpl;
import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.springmvc.InvalidRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.ResourceNotFoundProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssues;
import io.github.belgif.rest.problem.api.Problem;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.parameters.Parameter;

class InvalidRequestExceptionHandlerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    InvalidRequestExceptionHandler handler = new InvalidRequestExceptionHandler(OBJECT_MAPPER);

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

        assertInstanceOf(BadRequestProblem.class, problem);
        BadRequestProblem badRequestProblem = (BadRequestProblem) problem;
        assertEquals(InEnum.BODY, badRequestProblem.getIssues().get(0).getIn());
        assertEquals("/myProperty", badRequestProblem.getIssues().get(0).getName());
        assertEquals("myDummyValue", badRequestProblem.getIssues().get(0).getValue());
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

        assertInstanceOf(BadRequestProblem.class, problem);
        BadRequestProblem badRequestProblem = (BadRequestProblem) problem;
        assertEquals(InEnum.PATH, badRequestProblem.getIssues().get(0).getIn());
        assertEquals("myValue", badRequestProblem.getIssues().get(0).getValue());
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

        assertInstanceOf(BadRequestProblem.class, problem);
        BadRequestProblem badRequestProblem = (BadRequestProblem) problem;
        assertEquals(InEnum.QUERY, badRequestProblem.getIssues().get(0).getIn());
        assertEquals("myValue", badRequestProblem.getIssues().get(0).getValue());
        assertEquals("myParam", badRequestProblem.getIssues().get(0).getName());
        assertEquals("My Test Message", badRequestProblem.getIssues().get(0).getDetail());
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

        assertInstanceOf(BadRequestProblem.class, problem);
        BadRequestProblem badRequestProblem = (BadRequestProblem) problem;
        assertEquals(InEnum.HEADER, badRequestProblem.getIssues().get(0).getIn());
        assertEquals("myValue", badRequestProblem.getIssues().get(0).getValue());
        assertEquals("myParam", badRequestProblem.getIssues().get(0).getName());
        assertEquals("My Test Message", badRequestProblem.getIssues().get(0).getDetail());
    }

    @Test
    void missingPathResultsIn404Test() {
        List<ValidationReport.Message> messages = new ArrayList<>();
        messages.add(ValidationReport.Message.create("something.crazy.going.on", "My Test Message").build());
        messages.add(
                ValidationReport.Message.create("validation.request.path.missing", "Should result in a 404").build());

        InvalidRequestException exception = new InvalidRequestException(ValidationReport.from(messages));
        Problem problem = handler.handleInvalidRequestException(exception, null).getBody();
        assertInstanceOf(ResourceNotFoundProblem.class, problem);
        assertEquals(404, problem.getStatus());
    }

    @Test
    void defaultMessagesResultIn400Test() {
        List<ValidationReport.Message> messages = new ArrayList<>();
        messages.add(ValidationReport.Message.create("something.crazy.going.on", "My Test Message").build());
        messages.add(ValidationReport.Message.create("Something else", "test message").build());

        InvalidRequestException exception = new InvalidRequestException(ValidationReport.from(messages));
        HttpServletRequest request = new MockHttpServletRequest();

        Problem problem = handler.handleInvalidRequestException(exception, request).getBody();
        assertInstanceOf(BadRequestProblem.class, problem);
        assertEquals(400, problem.getStatus());
    }

    @Test
    void invalidJsonTest() {
        List<ValidationReport.Message> messages = new ArrayList<>();
        messages.add(ValidationReport.Message.create("validation.schema.invalidJson", "test message").build());

        InvalidRequestException exception = new InvalidRequestException(ValidationReport.from(messages));
        HttpServletRequest request = new MockHttpServletRequest();

        Problem problem = handler.handleInvalidRequestException(exception, request).getBody();
        assertInstanceOf(BadRequestProblem.class, problem);
        BadRequestProblem badRequestProblem = (BadRequestProblem) problem;
        assertEquals(InEnum.BODY, badRequestProblem.getIssues().get(0).getIn());
        assertEquals("Unable to parse JSON", badRequestProblem.getIssues().get(0).getDetail());
    }

    @Test
    void invalidJsonBodyTest() {
        List<ValidationReport.Message> messages = new ArrayList<>();
        messages.add(
                ValidationReport.Message.create("validation.request.body.schema.invalidJson", "test message").build());

        InvalidRequestException exception = new InvalidRequestException(ValidationReport.from(messages));
        HttpServletRequest request = new MockHttpServletRequest();

        Problem problem = handler.handleInvalidRequestException(exception, request).getBody();
        assertInstanceOf(BadRequestProblem.class, problem);
        BadRequestProblem badRequestProblem = (BadRequestProblem) problem;
        assertEquals(InEnum.BODY, badRequestProblem.getIssues().get(0).getIn());
        assertEquals("Unable to parse JSON", badRequestProblem.getIssues().get(0).getDetail());
    }

    @Test
    void unknownErrorTest() {
        List<ValidationReport.Message> messages = new ArrayList<>();
        messages.add(ValidationReport.Message.create("validation.schema.unknownError", "test message").build());

        InvalidRequestException exception = new InvalidRequestException(ValidationReport.from(messages));
        HttpServletRequest request = new MockHttpServletRequest();

        Problem problem = handler.handleInvalidRequestException(exception, request).getBody();
        assertInstanceOf(BadRequestProblem.class, problem);
        BadRequestProblem badRequestProblem = (BadRequestProblem) problem;
        assertEquals("An error occurred during schema validation", badRequestProblem.getIssues().get(0).getDetail());
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
        assertInstanceOf(BadRequestProblem.class, problem);
        BadRequestProblem badRequestProblem = (BadRequestProblem) problem;
        assertEquals(2, badRequestProblem.getIssues().size());
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
        assertInstanceOf(BadRequestProblem.class, problem);
        BadRequestProblem badRequestProblem = (BadRequestProblem) problem;
        assertEquals(1, badRequestProblem.getIssues().size());
    }

    @Test
    void defaultIssuesAreOfTypeSchemaViolationTest() {
        List<ValidationReport.Message> messages = new ArrayList<>();
        messages.add(ValidationReport.Message.create("my.violation", "test message").build());

        InvalidRequestException exception = new InvalidRequestException(ValidationReport.from(messages));
        HttpServletRequest request = new MockHttpServletRequest();

        Problem problem = handler.handleInvalidRequestException(exception, request).getBody();
        assertInstanceOf(BadRequestProblem.class, problem);
        BadRequestProblem badRequestProblem = (BadRequestProblem) problem;
        assertEquals(InputValidationIssues.ISSUE_TYPE_SCHEMA_VIOLATION, badRequestProblem.getIssues().get(0).getType());
    }

    @Test
    void missingQueryParamIssuesAreOfTypeUnknownInputTest() {
        List<ValidationReport.Message> messages = new ArrayList<>();
        messages.add(ValidationReport.Message.create("validation.request.parameter.query.unexpected", "test message")
                .build());

        InvalidRequestException exception = new InvalidRequestException(ValidationReport.from(messages));
        HttpServletRequest request = new MockHttpServletRequest();

        Problem problem = handler.handleInvalidRequestException(exception, request).getBody();
        assertInstanceOf(BadRequestProblem.class, problem);
        BadRequestProblem badRequestProblem = (BadRequestProblem) problem;
        assertEquals(InputValidationIssues.ISSUE_TYPE_UNKNOWN_INPUT, badRequestProblem.getIssues().get(0).getType());
    }

}
