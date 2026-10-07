package io.github.belgif.rest.problem.spring.boot.client;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.DefaultProblem;
import io.github.belgif.rest.problem.spring.ProblemMediaType;
import io.github.belgif.rest.problem.spring.client.ProblemResponseErrorHandler;

@ExtendWith(MockitoExtension.class)
abstract class AbstractProblemResponseErrorHandlerTest {

    private final ProblemResponseErrorHandler handler;

    @Mock
    private ClientHttpResponse response;

    @Mock
    private HttpHeaders headers;

    protected AbstractProblemResponseErrorHandlerTest(ProblemResponseErrorHandler handler) {
        this.handler = handler;
    }

    @Test
    void differentContentType() throws Exception {
        when(response.getHeaders()).thenReturn(headers);
        when(headers.getContentType()).thenReturn(MediaType.APPLICATION_XML);
        when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        assertThatExceptionOfType(HttpClientErrorException.BadRequest.class)
                .isThrownBy(() -> handler.handleError(URI.create("http://test"), HttpMethod.GET, response));
    }

    @Test
    void problemContentType() throws Exception {
        when(response.getHeaders()).thenReturn(headers);
        when(headers.getContentType()).thenReturn(ProblemMediaType.APPLICATION_PROBLEM_JSON);
        when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(response.getBody()).thenReturn(new ByteArrayInputStream(
                "{ \"type\": \"urn:problem-type:belgif:badRequest\" }".getBytes(StandardCharsets.UTF_8)));
        assertThatExceptionOfType(BadRequestProblem.class)
                .isThrownBy(() -> handler.handleError(URI.create("http://test"), HttpMethod.GET, response));
    }

    @Test
    void defaultProblem() throws Exception {
        when(response.getHeaders()).thenReturn(headers);
        when(headers.getContentType()).thenReturn(ProblemMediaType.APPLICATION_PROBLEM_JSON);
        when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(response.getBody()).thenReturn(new ByteArrayInputStream(
                "{ \"type\": \"urn:problem-type:belgif:foo\" }".getBytes(StandardCharsets.UTF_8)));
        assertThatExceptionOfType(DefaultProblem.class)
                .isThrownBy(() -> handler.handleError(URI.create("http://test"), HttpMethod.GET, response));
    }

    @Test
    void healthDown() throws Exception {
        when(response.getHeaders()).thenReturn(headers);
        when(headers.getContentType()).thenReturn(MediaType.APPLICATION_JSON);
        when(response.getStatusCode()).thenReturn(HttpStatus.SERVICE_UNAVAILABLE);
        when(response.getBody()).thenReturn(new ByteArrayInputStream(
                "{ \"status\": \"DOWN\" }".getBytes(StandardCharsets.UTF_8)));
        assertThatNoException()
                .isThrownBy(() -> handler.handleError(URI.create("http://test"), HttpMethod.GET, response));
    }

}
