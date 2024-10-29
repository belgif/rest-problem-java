package io.github.belgif.rest.problem.internal;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.atlassian.oai.validator.model.ApiOperation;
import com.atlassian.oai.validator.model.ApiPath;
import com.atlassian.oai.validator.model.ApiPathImpl;
import com.atlassian.oai.validator.report.ValidationReport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.belgif.rest.problem.api.InEnum;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.parameters.Parameter;

class InvalidRequestExceptionUtilTest {

    @Test
    void testGetInPath() {
        ValidationReport.Message.Builder builder =
                ValidationReport.Message.create("something.crazy.going.on", "My Test Message");
        builder.withContext(ValidationReport.MessageContext.create()
                .withParameter(new Parameter().in("path").name("myParam")).build());
        ValidationReport.Message message = builder.build();
        assertEquals(InEnum.PATH, InvalidRequestExceptionUtil.getIn(message));
    }

    @Test
    void testGetInHeader() {
        ValidationReport.Message.Builder builder =
                ValidationReport.Message.create("something.crazy.going.on", "My Test Message");
        builder.withContext(ValidationReport.MessageContext.create()
                .withParameter(new Parameter().in("header").name("myParam")).build());
        ValidationReport.Message message = builder.build();
        assertEquals(InEnum.HEADER, InvalidRequestExceptionUtil.getIn(message));
    }

    @Test
    void testGetInQuery() {
        ValidationReport.Message.Builder builder =
                ValidationReport.Message.create("something.crazy.going.on", "My Test Message");
        builder.withContext(ValidationReport.MessageContext.create()
                .withParameter(new Parameter().in("query").name("myParam")).build());
        ValidationReport.Message message = builder.build();
        assertEquals(InEnum.QUERY, InvalidRequestExceptionUtil.getIn(message));
    }

    @Test
    void testGetInBody() {
        ValidationReport.Message.Builder builder =
                ValidationReport.Message.create("something.crazy.going.on", "My Test Message");
        ValidationReport.Message message = builder.build();
        assertEquals(InEnum.BODY, InvalidRequestExceptionUtil.getIn(message));
    }

    @Test
    void testGetNameParameter() {
        ValidationReport.Message.Builder builder =
                ValidationReport.Message.create("something.crazy.going.on", "My Test Message");
        builder.withContext(ValidationReport.MessageContext.create()
                .withParameter(new Parameter().in("query").name("myParam")).build());
        ValidationReport.Message message = builder.build();
        assertEquals("myParam", InvalidRequestExceptionUtil.getName(message));
    }

    @Test
    void testGetNameBody() {
        ValidationReport.Message.Builder builder =
                ValidationReport.Message.create("something.crazy.going.on", "My Test Message");
        builder.withContext(ValidationReport.MessageContext.create()
                .withPointers("/myProperty", "yes")
                .build());
        ValidationReport.Message message = builder.build();
        assertEquals("/myProperty", InvalidRequestExceptionUtil.getName(message));
    }

    @Test
    void testGetPathValue() {
        ApiPath path = new ApiPathImpl("/{myFirstParam}/{mySecondParam}/pathSegment/{myThirdParam}/collection",
                "https://myPrefix.io");

        ValidationReport.Message.Builder builder =
                ValidationReport.Message.create("something.crazy.going.on", "My Test Message");
        builder.withContext(ValidationReport.MessageContext.create()
                .withApiOperation(new ApiOperation(path, path, PathItem.HttpMethod.GET, new Operation()))
                .withRequestPath("/firstValue/secondValue/pathSegment/thirdValue/collection").build());
        ValidationReport.Message message = builder.build();
        assertEquals("firstValue", InvalidRequestExceptionUtil.getPathValue(message, "myFirstParam"));
        assertEquals("secondValue", InvalidRequestExceptionUtil.getPathValue(message, "mySecondParam"));
        assertEquals("thirdValue", InvalidRequestExceptionUtil.getPathValue(message, "myThirdParam"));
    }

    @Test
    void testGetBodyValueWithEmptyReference() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String requestBody = "{\"myProperty\": \"myDummyValue\"}";
        request.setContent(requestBody.getBytes(StandardCharsets.UTF_8));
        request.setContentType("application/json");

        AtomicReference<JsonNode> reference = new AtomicReference<>();
        assertNull(reference.get());
        assertEquals("myDummyValue",
                InvalidRequestExceptionUtil.getBodyValue("/myProperty", reference, request, new ObjectMapper()));
        assertNotNull(reference.get());
    }

    @Test
    void testGetBodyValueWithReference() throws JsonProcessingException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String requestBody = "{\"myProperty\": \"myDummyValue\"}";
        JsonNode node = new ObjectMapper().readTree(requestBody);
        AtomicReference<JsonNode> reference = new AtomicReference<>(node);
        assertNotNull(reference.get());
        assertEquals("myDummyValue",
                InvalidRequestExceptionUtil.getBodyValue("/myProperty", reference, request, new ObjectMapper()));
    }

    @Test
    void testGetDetailForNormalMessages() {
        List<ValidationReport.Message> messages = new ArrayList<>();
        messages.add(ValidationReport.Message.create("validation.schema.firstError", "first issue").build());
        messages.add(ValidationReport.Message.create("validation.schema.secondError", "second issue").build());

        ValidationReport.Message message = ValidationReport.Message.create("my.validation.issue", "test message")
                .build().withNestedMessages(messages);

        assertEquals("test message", InvalidRequestExceptionUtil.getDetail(message));
    }

    @Test
    void testGetDetailForOneOfMessages() {
        List<ValidationReport.Message> messages = new ArrayList<>();
        messages.add(ValidationReport.Message.create("validation.schema.firstError", "first issue").build());
        messages.add(ValidationReport.Message.create("validation.schema.secondError", "second issue").build());

        ValidationReport.Message message =
                ValidationReport.Message.create("validation.request.body.schema.oneOf", "test message")
                        .build().withNestedMessages(messages);

        String detail = InvalidRequestExceptionUtil.getDetail(message);

        assertTrue(detail.contains("test message"));
        assertTrue(detail.contains("first issue"));
        assertTrue(detail.contains("second issue"));
    }

    @Test
    void testGetDetailForAnyOfMessages() {
        List<ValidationReport.Message> messages = new ArrayList<>();
        messages.add(ValidationReport.Message.create("validation.schema.firstError", "first issue").build());
        messages.add(ValidationReport.Message.create("validation.schema.secondError", "second issue").build());

        ValidationReport.Message message =
                ValidationReport.Message.create("validation.request.body.schema.anyOf", "test message")
                        .build().withNestedMessages(messages);

        String detail = InvalidRequestExceptionUtil.getDetail(message);

        assertTrue(detail.contains("test message"));
        assertTrue(detail.contains("first issue"));
        assertTrue(detail.contains("second issue"));
    }

}
