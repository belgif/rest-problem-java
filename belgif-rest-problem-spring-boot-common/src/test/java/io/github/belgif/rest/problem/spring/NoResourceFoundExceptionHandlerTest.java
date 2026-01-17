package io.github.belgif.rest.problem.spring;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import io.github.belgif.rest.problem.ResourceNotFoundProblem;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.spring.internal.ProblemRestControllerSupport;

class NoResourceFoundExceptionHandlerTest {

    private final NoResourceFoundExceptionHandler handler = new NoResourceFoundExceptionHandler();

    @Test
    void handleNoResourceFoundException() throws NoResourceFoundException {
        ResponseEntity<Problem> entity = handler.handleNoResourceFoundException(
                new NoResourceFoundException(HttpMethod.GET, "/test"));
        assertThat(entity.getStatusCode().value()).isEqualTo(404);
        assertThat(entity.getHeaders().getContentType()).isEqualTo(ProblemMediaType.INSTANCE);
        ResourceNotFoundProblem problem = (ResourceNotFoundProblem) entity.getBody();
        assertThat(problem.getDetail()).isEqualTo("No resource /test found");
    }

    @Test
    void handleNoResourceFoundExceptionAddsLeadingSlash() throws NoResourceFoundException {
        ResponseEntity<Problem> entity = handler.handleNoResourceFoundException(
                new NoResourceFoundException(HttpMethod.GET, "test"));
        assertThat(entity.getStatusCode().value()).isEqualTo(404);
        assertThat(entity.getHeaders().getContentType()).isEqualTo(ProblemMediaType.INSTANCE);
        ResourceNotFoundProblem problem = (ResourceNotFoundProblem) entity.getBody();
        assertThat(problem.getDetail()).isEqualTo("No resource /test found");
    }

    @Test
    void disabled() {
        try (MockedStatic<ProblemRestControllerSupport> mock = Mockito.mockStatic(ProblemRestControllerSupport.class)) {
            mock.when(ProblemRestControllerSupport::isServerSideDisabled).thenReturn(true);
            NoResourceFoundException exception = new NoResourceFoundException(HttpMethod.GET, "/test");
            assertThatThrownBy(() -> handler.handleNoResourceFoundException(exception)).isSameAs(exception);
        }
    }

}
