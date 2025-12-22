package io.github.belgif.rest.problem.spring;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.InternalServerErrorProblem;
import io.github.belgif.rest.problem.api.Problem;

class ProblemExceptionHandlerTest {

    private final ProblemExceptionHandler handler = new ProblemExceptionHandler();

    @Test
    void handleProblem() {
        ResponseEntity<Problem> response = handler.handleProblem(new BadRequestProblem());
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isInstanceOf(BadRequestProblem.class);
    }

    @Test
    void handleRuntimeException() {
        ResponseEntity<Problem> response = handler.handleException(new RuntimeException("runtime"));
        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isInstanceOf(InternalServerErrorProblem.class);
    }

    @Test
    void handleCheckedException() {
        ResponseEntity<Problem> response = handler.handleException(new Exception("checked"));
        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isInstanceOf(InternalServerErrorProblem.class);
    }

}
