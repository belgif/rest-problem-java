package io.github.belgif.rest.problem.jaxrs;

import static org.assertj.core.api.Assertions.*;

import javax.ejb.EJBException;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.InternalServerErrorProblem;

class EJBExceptionMapperTest {

    private final EJBExceptionMapper mapper = new EJBExceptionMapper();

    @Test
    void problemCause() {
        Response response = mapper.toResponse(new EJBException(new BadRequestProblem()));
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getEntity()).isInstanceOf(BadRequestProblem.class);
    }

    @Test
    void otherCause() {
        Response response = mapper.toResponse(new EJBException(new RuntimeException("other")));
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getEntity()).isInstanceOf(InternalServerErrorProblem.class);
    }

}
