package io.github.belgif.rest.problem.ee.client.jaxrs;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.function.Supplier;

import javax.enterprise.inject.Instance;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Providers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.DefaultProblem;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.ee.core.jaxrs.JaxRsUtil;
import io.github.belgif.rest.problem.ee.core.jaxrs.ProblemMediaType;

@ExtendWith(MockitoExtension.class)
class ProblemResponseExceptionMapperTest {

    @InjectMocks
    private ProblemResponseExceptionMapper mapper;

    @Mock
    private Response response;

    @Mock
    private Providers providers;

    @Mock
    private Instance<ObjectMapper> cdiObjectMapper;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    void problemMediaType() throws Exception {
        when(response.getMediaType()).thenReturn(ProblemMediaType.INSTANCE);
        JsonNode payload = JsonNodeFactory.instance.objectNode();
        when(response.readEntity(JsonNode.class)).thenReturn(payload);
        Problem problem = new BadRequestProblem();
        when(objectMapper.treeToValue(payload, Problem.class)).thenReturn(problem);
        assertThat(mapper.toThrowable(response)).isSameAs(problem);
    }

    @Test
    void jsonMediaTypeErrorStatus() throws Exception {
        when(response.getMediaType()).thenReturn(ProblemMediaType.APPLICATION_JSON_TYPE);
        when(response.getStatus()).thenReturn(400);
        JsonNode payload = JsonNodeFactory.instance.objectNode();
        when(response.readEntity(JsonNode.class)).thenReturn(payload);
        Problem problem = new BadRequestProblem();
        when(objectMapper.treeToValue(payload, Problem.class)).thenReturn(problem);
        assertThat(mapper.toThrowable(response)).isSameAs(problem);
    }

    @Test
    void jsonMediaTypeNoErrorStatus() {
        when(response.getMediaType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);
        when(response.getStatus()).thenReturn(200);
        assertThat(mapper.toThrowable(response)).isNull();
    }

    @Test
    void defaultProblem() throws Exception {
        when(response.getMediaType()).thenReturn(ProblemMediaType.APPLICATION_JSON_TYPE);
        when(response.getStatus()).thenReturn(400);
        JsonNode payload = JsonNodeFactory.instance.objectNode();
        when(response.readEntity(JsonNode.class)).thenReturn(payload);
        Problem problem = new DefaultProblem(URI.create("type"), URI.create("href"), "Title", 400);
        when(objectMapper.treeToValue(payload, Problem.class)).thenReturn(problem);
        assertThat(mapper.toThrowable(response)).isSameAs(problem);
    }

    @Test
    void healthDownResponse() {
        when(response.getMediaType()).thenReturn(ProblemMediaType.APPLICATION_JSON_TYPE);
        when(response.getStatus()).thenReturn(503);
        when(response.getStatusInfo()).thenReturn(Response.Status.SERVICE_UNAVAILABLE);
        JsonNode payload = JsonNodeFactory.instance.objectNode().put("status", "DOWN");
        when(response.readEntity(JsonNode.class)).thenReturn(payload);
        WebApplicationException exception = (WebApplicationException) mapper.toThrowable(response);
        assertThat(exception.getResponse()).isSameAs(response);
    }

    @Test
    void exceptionReadingProblem() throws Exception {
        when(response.getMediaType()).thenReturn(ProblemMediaType.INSTANCE);
        JsonNode payload = JsonNodeFactory.instance.objectNode();
        when(response.readEntity(JsonNode.class)).thenReturn(payload);
        when(objectMapper.treeToValue(payload, Problem.class)).thenThrow(new RuntimeException("oops"));
        assertThat(mapper.toThrowable(response)).isNull();
    }

    @Test
    void differentMediaType() {
        when(response.getMediaType()).thenReturn(MediaType.APPLICATION_XML_TYPE);
        when(response.getStatus()).thenReturn(400);
        assertThat(mapper.toThrowable(response)).isNull();
    }

    @Test
    void init() throws Exception {
        Field objectMapperField = ProblemResponseExceptionMapper.class.getDeclaredField("objectMapper");
        objectMapperField.setAccessible(true);
        objectMapperField.set(mapper, null);
        ObjectMapper newMapper = new ObjectMapper();
        try (MockedStatic<JaxRsUtil> mock = mockStatic(JaxRsUtil.class)) {
            mock.when(() -> JaxRsUtil.locateObjectMapper(eq(providers), eq(cdiObjectMapper), eq(Problem.class),
                    eq(MediaType.APPLICATION_JSON_TYPE), any(Supplier.class))).thenReturn(newMapper);
            mapper.init();
        }
        assertThat(mapper).hasFieldOrPropertyWithValue("objectMapper", newMapper);
        mapper.init();
        assertThat(mapper).hasFieldOrPropertyWithValue("objectMapper", newMapper);
    }

}
