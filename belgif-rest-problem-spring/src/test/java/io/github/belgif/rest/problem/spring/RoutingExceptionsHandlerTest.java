package io.github.belgif.rest.problem.spring;

import static io.github.belgif.rest.problem.api.InputValidationIssues.*;
import static org.assertj.core.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.client.RestTemplate;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.InternalServerErrorProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.Problem;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.exc.MismatchedInputException;

class RoutingExceptionsHandlerTest {

    private final RoutingExceptionsHandler handler = new RoutingExceptionsHandler();

    @Test
    void handleMissingServletRequestParameterException() {
        ResponseEntity<Problem> entity = handler.handleMissingServletRequestParameterException(
                new MissingServletRequestParameterException("name", "String"));
        assertThat(entity.getStatusCode().value()).isEqualTo(400);
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
        assertThat(entity.getStatusCode().value()).isEqualTo(400);
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
    void handleHttpMessageNotReadableJacksonMismatchedInputException() {
        MismatchedInputException exception = MismatchedInputException.from(null, Object.class, "detail");
        exception.prependPath(new JacksonException.Reference(null, "id"));
        ResponseEntity<Problem> entity =
                handler.handleHttpMessageNotReadable(
                        new HttpMessageNotReadableException(null, exception, new MockHttpInputMessage(new byte[0])));
        assertThat(entity.getStatusCode().value()).isEqualTo(400);
        assertThat(entity.getHeaders().getContentType()).isEqualTo(ProblemMediaType.INSTANCE);
        assertThat(entity.getBody()).isInstanceOfSatisfying(BadRequestProblem.class, problem -> {
            assertThat(problem.getIssues()).hasSize(1);
            assertThat(problem.getIssues().get(0).getType())
                    .hasToString("urn:problem-type:belgif:input-validation:schemaViolation");
            assertThat(problem.getIssues().get(0).getIn()).isEqualTo(InEnum.BODY);
            assertThat(problem.getIssues().get(0).getName()).isEqualTo("id");
            assertThat(problem.getIssues().get(0).getValue()).isNull();
            assertThat(problem.getIssues().get(0).getDetail()).isEqualTo("detail");
        });
    }

    @Test
    void handleHttpMessageNotReadableJacksonMismatchedInputExceptionFromRestTemplate() {
        MismatchedInputException exception = MismatchedInputException.from(null, Object.class, "detail");
        exception.prependPath(new JacksonException.Reference(null, "id"));
        exception.setStackTrace(
                new StackTraceElement[] { new StackTraceElement(RestTemplate.class.getName(), "test", "test", 1) });
        ResponseEntity<Problem> entity =
                handler.handleHttpMessageNotReadable(
                        new HttpMessageNotReadableException(null, exception, new MockHttpInputMessage(new byte[0])));
        assertThat(entity.getStatusCode().value()).isEqualTo(500);
        assertThat(entity.getHeaders().getContentType()).isEqualTo(ProblemMediaType.INSTANCE);
        assertThat(entity.getBody()).isInstanceOf(InternalServerErrorProblem.class);
    }

    @Test
    void handleHttpMessageNotReadableRequiredRequestBodyIsMissing() {
        ResponseEntity<Problem> entity = handler.handleHttpMessageNotReadable(
                new HttpMessageNotReadableException("Required request body is missing: " +
                        "public org.springframework.http.ResponseEntity<java.lang.String> " +
                        "io.github.belgif.rest.problem.FrontendController.beanValidationBody" +
                        "(io.github.belgif.rest.problem.model.Model)",
                        new MockHttpInputMessage("dummy".getBytes(StandardCharsets.UTF_8))));
        assertThat(entity.getStatusCode().value()).isEqualTo(400);
        assertThat(entity.getHeaders().getContentType()).isEqualTo(ProblemMediaType.INSTANCE);
        assertThat(entity.getBody()).isInstanceOfSatisfying(BadRequestProblem.class, problem -> {
            assertThat(problem.getIssues()).hasSize(1);
            assertThat(problem.getIssues().get(0).getType()).isEqualTo(ISSUE_TYPE_SCHEMA_VIOLATION);
            assertThat(problem.getIssues().get(0).getIn()).isEqualTo(InEnum.BODY);
            assertThat(problem.getIssues().get(0).getName()).isNull();
            assertThat(problem.getIssues().get(0).getDetail()).isEqualTo("Required request body is missing");
        });
    }

    @Test
    void handleHttpMessageNotReadableJsonParseError() {
        ResponseEntity<Problem> entity = handler.handleHttpMessageNotReadable(
                new HttpMessageNotReadableException("JSON parse error: " +
                        "Cannot deserialize value of type `java.time.LocalDate` " +
                        "from String \"12/07/1991\": Failed to deserialize java.time.LocalDate: " +
                        "(java.time.format.DateTimeParseException) Text '12/07/1991' could not be parsed at index 0",
                        new MockHttpInputMessage("dummy".getBytes(StandardCharsets.UTF_8))));
        assertThat(entity.getStatusCode().value()).isEqualTo(400);
        assertThat(entity.getHeaders().getContentType()).isEqualTo(ProblemMediaType.INSTANCE);
        assertThat(entity.getBody()).isInstanceOfSatisfying(BadRequestProblem.class, problem -> {
            assertThat(problem.getIssues()).hasSize(1);
            assertThat(problem.getIssues().get(0).getType()).isEqualTo(ISSUE_TYPE_SCHEMA_VIOLATION);
            assertThat(problem.getIssues().get(0).getIn()).isEqualTo(InEnum.BODY);
            assertThat(problem.getIssues().get(0).getName()).isNull();
            assertThat(problem.getIssues().get(0).getDetail()).isEqualTo("JSON parse error");
        });
    }

    @Test
    void handleHttpMessageNotReadableOther() {
        ResponseEntity<Problem> entity = handler.handleHttpMessageNotReadable(
                new HttpMessageNotReadableException("Other non-sanitized message",
                        new MockHttpInputMessage("dummy".getBytes(StandardCharsets.UTF_8))));
        assertThat(entity.getStatusCode().value()).isEqualTo(400);
        assertThat(entity.getHeaders().getContentType()).isEqualTo(ProblemMediaType.INSTANCE);
        assertThat(entity.getBody()).isInstanceOfSatisfying(BadRequestProblem.class, problem -> {
            assertThat(problem.getIssues()).hasSize(1);
            assertThat(problem.getIssues().get(0).getType()).isEqualTo(ISSUE_TYPE_SCHEMA_VIOLATION);
            assertThat(problem.getIssues().get(0).getIn()).isEqualTo(InEnum.BODY);
            assertThat(problem.getIssues().get(0).getName()).isNull();
            assertThat(problem.getIssues().get(0).getDetail()).isNull();
        });
    }

    @Test
    void handleHttpRequestMethodNotSupported() {
        ResponseEntity<Void> response = handler.handleHttpRequestMethodNotSupported(
                new HttpRequestMethodNotSupportedException("POST", Arrays.asList("GET", "PUT")));
        assertThat(response.getStatusCode().value()).isEqualTo(405);
        assertThat(response.getHeaders().getAllow()).containsExactly(HttpMethod.GET, HttpMethod.PUT);
    }

    @Test
    void handleHttpMediaTypeNotAcceptable() {
        ResponseEntity<Void> response = handler.handleHttpMediaTypeNotAcceptable(
                new HttpMediaTypeNotAcceptableException(Collections.singletonList(MediaType.APPLICATION_JSON)));
        assertThat(response.getStatusCode().value()).isEqualTo(406);
    }

    @Test
    void handleHttpMediaTypeNotSupported() {
        ResponseEntity<Void> response = handler.handleHttpMediaTypeNotSupported(
                new HttpMediaTypeNotSupportedException(MediaType.APPLICATION_XML,
                        Collections.singletonList(MediaType.APPLICATION_JSON)));
        assertThat(response.getStatusCode().value()).isEqualTo(415);
    }

}
