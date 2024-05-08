package io.github.belgif.rest.problem.jaxrs;

import static org.assertj.core.api.Assertions.*;

import java.util.Collections;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.TooManyRequestsProblem;

class ProblemMediaTypeTest {

    @Test
    void mediaType() {
        assertThat(ProblemMediaType.INSTANCE.getType()).isEqualTo("application");
        assertThat(ProblemMediaType.INSTANCE.getSubtype()).isEqualTo("problem+json");
    }

    @Test
    void toResponse() {
        BadRequestProblem problem = new BadRequestProblem();
        Response response = ProblemMediaType.INSTANCE.toResponse(problem);
        assertThat(response.getEntity()).isEqualTo(problem);
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getMediaType()).isEqualTo(ProblemMediaType.INSTANCE);
    }

    @Test
    void toResponseWithHeaders() {
        TooManyRequestsProblem problem = new TooManyRequestsProblem();
        problem.setRetryAfterSec(120L);
        Response response = ProblemMediaType.INSTANCE.toResponse(problem);
        assertThat(response.getEntity()).isEqualTo(problem);
        assertThat(response.getStatus()).isEqualTo(429);
        assertThat(response.getMediaType()).isEqualTo(ProblemMediaType.INSTANCE);
        assertThat(response.getHeaders()).containsEntry("Retry-After", Collections.singletonList(120L));
    }

}
