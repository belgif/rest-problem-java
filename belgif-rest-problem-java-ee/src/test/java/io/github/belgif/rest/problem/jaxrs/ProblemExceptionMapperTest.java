package io.github.belgif.rest.problem.jaxrs;

import static org.assertj.core.api.Assertions.*;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.BadRequestProblem;

class ProblemExceptionMapperTest {

    private final ProblemExceptionMapper mapper = new ProblemExceptionMapper();

    @Test
    void map() {
        Response response = mapper.toResponse(new BadRequestProblem());
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getEntity()).isInstanceOf(BadRequestProblem.class);
    }

}
