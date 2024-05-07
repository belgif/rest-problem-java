package io.github.belgif.rest.problem.jaxrs.client;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.DefaultProblem;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.jaxrs.ProblemMediaType;

@ExtendWith(MockitoExtension.class)
class ProblemResponseExceptionMapperTest {

    private final ProblemResponseExceptionMapper mapper = new ProblemResponseExceptionMapper();

    @Mock
    private Response response;

    @Test
    void problemMediaType() {
        when(response.getMediaType()).thenReturn(ProblemMediaType.INSTANCE);
        Problem problem = new BadRequestProblem();
        when(response.readEntity(Problem.class)).thenReturn(problem);
        assertThat(mapper.toThrowable(response)).isSameAs(problem);
    }

    @Test
    void jsonMediaTypeErrorStatus() {
        when(response.getMediaType()).thenReturn(ProblemMediaType.APPLICATION_JSON_TYPE);
        when(response.getStatus()).thenReturn(400);
        Problem problem = new BadRequestProblem();
        when(response.readEntity(Problem.class)).thenReturn(problem);
        assertThat(mapper.toThrowable(response)).isSameAs(problem);
    }

    @Test
    void jsonMediaTypeNoErrorStatus() {
        when(response.getMediaType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);
        when(response.getStatus()).thenReturn(200);
        assertThat(mapper.toThrowable(response)).isNull();
    }

    @Test
    void defaultProblem() {
        when(response.getMediaType()).thenReturn(ProblemMediaType.APPLICATION_JSON_TYPE);
        when(response.getStatus()).thenReturn(400);
        Problem problem = new DefaultProblem(URI.create("type"), URI.create("href"), "Title", 400);
        when(response.readEntity(Problem.class)).thenReturn(problem);
        assertThat(mapper.toThrowable(response)).isSameAs(problem);
    }

    @Test
    void differentMediaType() {
        when(response.getMediaType()).thenReturn(MediaType.APPLICATION_XML_TYPE);
        when(response.getStatus()).thenReturn(400);
        assertThat(mapper.toThrowable(response)).isNull();
    }

}
