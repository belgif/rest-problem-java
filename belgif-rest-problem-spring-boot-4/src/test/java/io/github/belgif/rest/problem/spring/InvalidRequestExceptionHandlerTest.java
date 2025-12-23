package io.github.belgif.rest.problem.spring;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.springmvc.InvalidRequestException;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.Problem;
import tools.jackson.databind.ObjectMapper;

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

}
