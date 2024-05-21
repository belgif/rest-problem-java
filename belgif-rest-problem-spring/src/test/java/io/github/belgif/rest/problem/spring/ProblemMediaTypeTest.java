package io.github.belgif.rest.problem.spring;

import static org.assertj.core.api.Assertions.*;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.TooManyRequestsProblem;
import io.github.belgif.rest.problem.api.Problem;

class ProblemMediaTypeTest {

    @Test
    void mediaType() {
        assertThat(ProblemMediaType.INSTANCE.getType()).isEqualTo("application");
        assertThat(ProblemMediaType.INSTANCE.getSubtype()).isEqualTo("problem+json");
    }

    @Test
    void toResponse() {
        BadRequestProblem problem = new BadRequestProblem();
        ResponseEntity<Problem> response = ProblemMediaType.INSTANCE.toResponse(problem);
        assertThat(response.getBody()).isEqualTo(problem);
        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getHeaders().getContentType()).isEqualTo(ProblemMediaType.INSTANCE);
    }

    @Test
    void toResponseWithHeaders() {
        TooManyRequestsProblem problem = new TooManyRequestsProblem();
        problem.setRetryAfterSec(120L);
        ResponseEntity<Problem> response = ProblemMediaType.INSTANCE.toResponse(problem);
        assertThat(response.getBody()).isEqualTo(problem);
        assertThat(response.getStatusCodeValue()).isEqualTo(429);
        assertThat(response.getHeaders().getContentType()).isEqualTo(ProblemMediaType.INSTANCE);
        assertThat(response.getHeaders()).containsEntry("Retry-After", Collections.singletonList("120"));
    }

}
