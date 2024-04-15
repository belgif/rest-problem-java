package io.github.belgif.rest.problem.jaxrs;

import static org.assertj.core.api.Assertions.*;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

class WebApplicationExceptionMapperTest {

    private final WebApplicationExceptionMapper mapper = new WebApplicationExceptionMapper();

    @Test
    void clientError() {
        Response response = mapper.toResponse(new WebApplicationException(400));
        assertThat(response.getStatus()).isEqualTo(400);
    }

    @Test
    void serverError() {
        Response response = mapper.toResponse(new WebApplicationException(500));
        assertThat(response.getStatus()).isEqualTo(500);
    }

}
