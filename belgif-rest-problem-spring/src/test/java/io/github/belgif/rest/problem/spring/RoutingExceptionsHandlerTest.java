package io.github.belgif.rest.problem.spring;

import static io.github.belgif.rest.problem.api.InputValidationIssues.*;
import static org.assertj.core.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.Problem;

class RoutingExceptionsHandlerTest {

    private final RoutingExceptionsHandler handler = new RoutingExceptionsHandler();

    @Test
    void handleMissingServletRequestParameterException() {
        ResponseEntity<Problem> entity = handler.handleMissingServletRequestParameterException(
                new MissingServletRequestParameterException("name", "String"));
        assertThat(entity.getStatusCodeValue()).isEqualTo(400);
        assertThat(entity.getHeaders().getContentType()).isEqualTo(ProblemMediaType.INSTANCE);
        assertThat(entity.getBody()).isInstanceOfSatisfying(BadRequestProblem.class, problem -> {
            assertThat(problem.getIssues()).hasSize(1);
            assertThat(problem.getIssues().get(0).getType()).isEqualTo(ISSUE_TYPE_SCHEMA_VIOLATION);
            assertThat(problem.getIssues().get(0).getIn()).isEqualTo(InEnum.QUERY);
            assertThat(problem.getIssues().get(0).getName()).isEqualTo("name");
            assertThat(problem.getIssues().get(0).getDetail()).isEqualTo(
                    "Required request parameter 'name' for method parameter type String is not present");
        });
    }

    @Test
    void handleMissingRequestHeaderException() throws Exception {
        ResponseEntity<Problem> entity = handler.handleMissingRequestHeaderException(
                new MissingRequestHeaderException("name", MethodParameter.forParameter(
                        Objects.class.getMethod("toString", Object.class).getParameters()[0])));
        assertThat(entity.getStatusCodeValue()).isEqualTo(400);
        assertThat(entity.getHeaders().getContentType()).isEqualTo(ProblemMediaType.INSTANCE);
        assertThat(entity.getBody()).isInstanceOfSatisfying(BadRequestProblem.class, problem -> {
            assertThat(problem.getIssues()).hasSize(1);
            assertThat(problem.getIssues().get(0).getType()).isEqualTo(ISSUE_TYPE_SCHEMA_VIOLATION);
            assertThat(problem.getIssues().get(0).getIn()).isEqualTo(InEnum.HEADER);
            assertThat(problem.getIssues().get(0).getName()).isEqualTo("name");
            assertThat(problem.getIssues().get(0).getDetail()).isEqualTo(
                    "Required request header 'name' for method parameter type Object is not present");
        });
    }

    @Test
    void handleHttpMessageNotReadable() {
        ResponseEntity<Problem> entity = handler.handleHttpMessageNotReadable(
                new HttpMessageNotReadableException("message",
                        new MockHttpInputMessage("dummy".getBytes(StandardCharsets.UTF_8))));
        assertThat(entity.getStatusCodeValue()).isEqualTo(400);
        assertThat(entity.getHeaders().getContentType()).isEqualTo(ProblemMediaType.INSTANCE);
        assertThat(entity.getBody()).isInstanceOfSatisfying(BadRequestProblem.class, problem -> {
            assertThat(problem.getIssues()).hasSize(1);
            assertThat(problem.getIssues().get(0).getType()).isEqualTo(ISSUE_TYPE_SCHEMA_VIOLATION);
            assertThat(problem.getIssues().get(0).getIn()).isEqualTo(InEnum.BODY);
            assertThat(problem.getIssues().get(0).getName()).isNull();
            assertThat(problem.getIssues().get(0).getDetail()).isEqualTo("message");
        });
    }

}
