package io.github.belgif.rest.problem.ee.jaxrs;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.belgif.rest.problem.InternalServerErrorProblem;
import io.github.belgif.rest.problem.ee.jaxrs.AbstractProblemExceptionMapperTest.ProblemsDisabledResource;
import io.github.belgif.rest.problem.ee.jaxrs.AbstractProblemExceptionMapperTest.ProblemsEnabledResource;

@ExtendWith(MockitoExtension.class)
class DefaultExceptionMapperTest {

    @Mock
    private ResourceInfo resourceInfo;

    @InjectMocks
    private DefaultExceptionMapper mapper;

    @Test
    void runtimeException() {
        when(resourceInfo.getResourceClass()).thenReturn((Class) ProblemsEnabledResource.class);
        Response response = mapper.toResponse(new RuntimeException("runtime"));
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getEntity()).isInstanceOf(InternalServerErrorProblem.class);
    }

    @Test
    void checkedException() {
        when(resourceInfo.getResourceClass()).thenReturn((Class) ProblemsEnabledResource.class);
        Response response = mapper.toResponse(new Exception("checked"));
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getEntity()).isInstanceOf(InternalServerErrorProblem.class);
    }

    @Test
    void disabledRuntimeException() {
        when(resourceInfo.getResourceClass()).thenReturn((Class) ProblemsDisabledResource.class);
        RuntimeException exception = new RuntimeException("runtime");
        assertThatException().isThrownBy(() -> mapper.toResponse(exception)).isEqualTo(exception);
    }

    @Test
    void disabledCheckedException() {
        when(resourceInfo.getResourceClass()).thenReturn((Class) ProblemsDisabledResource.class);
        Exception exception = new Exception("checked");
        assertThatException().isThrownBy(() -> mapper.toResponse(exception)).isEqualTo(exception);
    }
}
