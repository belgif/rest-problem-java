package io.github.belgif.rest.problem.jaxrs.client;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.DefaultProblem;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.jaxrs.ProblemMediaType;

@ExtendWith(MockitoExtension.class)
class ProblemClientResponseFilterTest {

    @InjectMocks
    private ProblemClientResponseFilter filter;

    @Mock
    private ClientRequestContext requestContext;

    @Mock
    private ClientResponseContext responseContext;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    void problemMediaType() throws Exception {
        when(responseContext.getMediaType()).thenReturn(ProblemMediaType.INSTANCE);
        InputStream entityStream = new ByteArrayInputStream("dummy".getBytes(StandardCharsets.UTF_8));
        when(responseContext.getEntityStream()).thenReturn(entityStream);
        Problem problem = new BadRequestProblem();
        when(objectMapper.readValue(entityStream, Problem.class)).thenReturn(problem);
        assertThatExceptionOfType(ProblemWrapper.class).isThrownBy(
                () -> filter.filter(requestContext, responseContext))
                .extracting(ProblemWrapper::getProblem)
                .isEqualTo(problem);
    }

    @Test
    void jsonMediaTypeErrorStatus() throws Exception {
        when(responseContext.getMediaType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);
        when(responseContext.getStatus()).thenReturn(400);
        InputStream entityStream = new ByteArrayInputStream("dummy".getBytes(StandardCharsets.UTF_8));
        when(responseContext.getEntityStream()).thenReturn(entityStream);
        Problem problem = new BadRequestProblem();
        when(objectMapper.readValue(entityStream, Problem.class)).thenReturn(problem);
        assertThatExceptionOfType(ProblemWrapper.class).isThrownBy(
                () -> filter.filter(requestContext, responseContext))
                .extracting(ProblemWrapper::getProblem)
                .isEqualTo(problem);
    }

    @Test
    void jsonMediaTypeNoErrorStatus() {
        when(responseContext.getMediaType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);
        when(responseContext.getStatus()).thenReturn(200);

        assertThatNoException().isThrownBy(
                () -> filter.filter(requestContext, responseContext));
    }

    @Test
    void defaultProblem() throws Exception {
        when(responseContext.getMediaType()).thenReturn(ProblemMediaType.INSTANCE);
        InputStream entityStream = new ByteArrayInputStream("dummy".getBytes(StandardCharsets.UTF_8));
        when(responseContext.getEntityStream()).thenReturn(entityStream);
        Problem problem = new DefaultProblem(URI.create("type"), URI.create("href"), "Title", 400);
        when(objectMapper.readValue(entityStream, Problem.class)).thenReturn(problem);
        assertThatExceptionOfType(ProblemWrapper.class).isThrownBy(
                () -> filter.filter(requestContext, responseContext))
                .extracting(ProblemWrapper::getProblem)
                .isEqualTo(problem);
    }

    @Test
    void differentMediaType() {
        when(responseContext.getMediaType()).thenReturn(MediaType.APPLICATION_XML_TYPE);
        when(responseContext.getStatus()).thenReturn(400);
        assertThatNoException().isThrownBy(
                () -> filter.filter(requestContext, responseContext));
    }

}
