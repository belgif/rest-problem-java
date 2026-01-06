package io.github.belgif.rest.problem.spring;

import static org.assertj.core.api.Assertions.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.springmvc.InvalidRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.Problem;

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

        assertThat(problem).isInstanceOf(BadRequestProblem.class);
        BadRequestProblem badRequestProblem = (BadRequestProblem) problem;
        assertThat(badRequestProblem.getIssues().get(0).getIn()).isEqualTo(InEnum.BODY);
        assertThat(badRequestProblem.getIssues().get(0).getName()).isEqualTo("/myProperty");
        assertThat(badRequestProblem.getIssues().get(0).getValue()).isEqualTo("myDummyValue");
    }

}
