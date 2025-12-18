package io.github.belgif.rest.problem.spring;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import io.github.belgif.rest.problem.ResourceNotFoundProblem;
import io.github.belgif.rest.problem.api.Problem;

class NoResourceFoundExceptionHandlerTest {

    private final NoResourceFoundExceptionHandler handler = new NoResourceFoundExceptionHandler();

    @Test
    void handleNoResourceFoundException() {
        ResponseEntity<Problem> entity = handler.handleNoResourceFoundException(
                new NoResourceFoundException(HttpMethod.GET, "api/v1/test", "/test"));
        assertThat(entity.getStatusCode().value()).isEqualTo(404);
        assertThat(entity.getHeaders().getContentType()).isEqualTo(ProblemMediaType.INSTANCE);
        ResourceNotFoundProblem problem = (ResourceNotFoundProblem) entity.getBody();
        assertThat(problem.getDetail()).isEqualTo("No resource /test found");
    }

    @Test
    void handleNoResourceFoundExceptionAddsLeadingSlash() {
        ResponseEntity<Problem> entity = handler.handleNoResourceFoundException(
                new NoResourceFoundException(HttpMethod.GET, "api/v1/test", "test"));
        assertThat(entity.getStatusCode().value()).isEqualTo(404);
        assertThat(entity.getHeaders().getContentType()).isEqualTo(ProblemMediaType.INSTANCE);
        ResourceNotFoundProblem problem = (ResourceNotFoundProblem) entity.getBody();
        assertThat(problem.getDetail()).isEqualTo("No resource /test found");
    }

}
