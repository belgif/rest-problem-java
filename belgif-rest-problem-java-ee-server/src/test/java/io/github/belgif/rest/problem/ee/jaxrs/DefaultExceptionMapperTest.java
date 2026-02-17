package io.github.belgif.rest.problem.ee.jaxrs;

import static org.assertj.core.api.Assertions.*;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.InternalServerErrorProblem;

class DefaultExceptionMapperTest {

    private final DefaultExceptionMapper mapper = new DefaultExceptionMapper();

    @Test
    void runtimeException() {
        Response response = mapper.toResponse(new RuntimeException("runtime"));
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getEntity()).isInstanceOf(InternalServerErrorProblem.class);
    }

    @Test
    void checkedException() {
        Response response = mapper.toResponse(new Exception("checked"));
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getEntity()).isInstanceOf(InternalServerErrorProblem.class);
    }

}
