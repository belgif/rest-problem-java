package io.github.belgif.rest.problem.ee.jaxrs;

import static org.assertj.core.api.Assertions.*;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.github.belgif.rest.problem.BadRequestProblem;

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

    @Test
    void causedByJsonParseException() {
        Response response =
                mapper.toResponse(new WebApplicationException(new JsonParseException(null, "")));
        assertThat(response.getEntity()).isInstanceOf(BadRequestProblem.class);
    }

    @Test
    void causedByJsonMappingException() {
        Response response =
                mapper.toResponse(new WebApplicationException(new JsonMappingException(null, "")));
        assertThat(response.getEntity()).isInstanceOf(BadRequestProblem.class);
    }

}
