package io.github.belgif.rest.problem.ee.jaxrs;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.ejb.EJBException;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.InternalServerErrorProblem;
import io.github.belgif.rest.problem.ee.jaxrs.AbstractProblemExceptionMapperTest.ProblemsDisabledResource;
import io.github.belgif.rest.problem.ee.jaxrs.AbstractProblemExceptionMapperTest.ProblemsEnabledResource;

@ExtendWith(MockitoExtension.class)
class EJBExceptionMapperTest {

    @Mock
    private ResourceInfo resourceInfo;

    @InjectMocks
    private EJBExceptionMapper mapper;

    @Test
    void problemCause() {
        when(resourceInfo.getResourceClass()).thenReturn((Class) ProblemsEnabledResource.class);
        Response response = mapper.toResponse(new EJBException(new BadRequestProblem()));
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getEntity()).isInstanceOf(BadRequestProblem.class);
    }

    @Test
    void otherCause() {
        when(resourceInfo.getResourceClass()).thenReturn((Class) ProblemsEnabledResource.class);
        Response response = mapper.toResponse(new EJBException(new RuntimeException("other")));
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getEntity()).isInstanceOf(InternalServerErrorProblem.class);
    }

    @Test
    void disabled() {
        when(resourceInfo.getResourceClass()).thenReturn((Class) ProblemsDisabledResource.class);
        EJBException exception = new EJBException(new BadRequestProblem());
        assertThatException().isThrownBy(() -> mapper.toResponse(exception)).isEqualTo(exception);
    }

}
