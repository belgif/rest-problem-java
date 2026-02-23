package io.github.belgif.rest.problem.spring.client;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.DefaultProblem;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.spring.ProblemMediaType;
import io.github.belgif.rest.problem.spring.client.AbstractProblemResponseErrorHandler.ProblemReader;

@ExtendWith(MockitoExtension.class)
class AbstractProblemResponseErrorHandlerTest {

    @Mock
    private ProblemReader problemReader;

    @InjectMocks
    private ProblemResponseErrorHandler handler;

    @Test
    void problemMediaType() throws Exception {
        InputStream entityStream = new ByteArrayInputStream("dummy".getBytes(StandardCharsets.UTF_8));
        MockClientHttpResponse response = new MockClientHttpResponse(entityStream, 400);
        response.getHeaders().setContentType(ProblemMediaType.INSTANCE);

        Problem problem = new BadRequestProblem();
        when(problemReader.read(entityStream)).thenReturn(problem);
        assertThatExceptionOfType(BadRequestProblem.class)
                .isThrownBy(() -> handler.handleError(URI.create("test"), HttpMethod.GET, response))
                .isEqualTo(problem);
    }

    @Test
    void jsonMediaTypeErrorStatus() throws Exception {
        InputStream entityStream = new ByteArrayInputStream("dummy".getBytes(StandardCharsets.UTF_8));
        MockClientHttpResponse response = new MockClientHttpResponse(entityStream, 400);
        response.getHeaders().setContentType(ProblemMediaType.APPLICATION_JSON);

        Problem problem = new BadRequestProblem();
        when(problemReader.read(entityStream)).thenReturn(problem);
        assertThatExceptionOfType(BadRequestProblem.class)
                .isThrownBy(() -> handler.handleError(URI.create("test"), HttpMethod.GET, response))
                .isEqualTo(problem);
    }

    @Test
    void jsonMediaTypeNoErrorStatus() {
        InputStream entityStream = new ByteArrayInputStream("dummy".getBytes(StandardCharsets.UTF_8));
        MockClientHttpResponse response = new MockClientHttpResponse(entityStream, 200);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        assertThatExceptionOfType(UnknownHttpStatusCodeException.class)
                .isThrownBy(() -> handler.handleError(URI.create("test"), HttpMethod.GET, response));
    }

    @Test
    void defaultProblem() throws Exception {
        InputStream entityStream = new ByteArrayInputStream("dummy".getBytes(StandardCharsets.UTF_8));
        MockClientHttpResponse response = new MockClientHttpResponse(entityStream, 400);
        response.getHeaders().setContentType(ProblemMediaType.INSTANCE);

        Problem problem = new DefaultProblem(URI.create("type"), URI.create("href"), "Title", 400);
        when(problemReader.read(entityStream)).thenReturn(problem);
        assertThatExceptionOfType(DefaultProblem.class)
                .isThrownBy(() -> handler.handleError(URI.create("test"), HttpMethod.GET, response))
                .isEqualTo(problem);
    }

    @Test
    void differentMediaType() {
        InputStream entityStream = new ByteArrayInputStream("dummy".getBytes(StandardCharsets.UTF_8));
        MockClientHttpResponse response = new MockClientHttpResponse(entityStream, 400);
        response.getHeaders().setContentType(MediaType.APPLICATION_XML);

        assertThatExceptionOfType(HttpClientErrorException.BadRequest.class)
                .isThrownBy(() -> handler.handleError(URI.create("test"), HttpMethod.GET, response));
    }

    public static class ProblemResponseErrorHandler extends AbstractProblemResponseErrorHandler {

        ProblemResponseErrorHandler(ProblemReader problemReader) {
            super(problemReader);
        }

    }

}
