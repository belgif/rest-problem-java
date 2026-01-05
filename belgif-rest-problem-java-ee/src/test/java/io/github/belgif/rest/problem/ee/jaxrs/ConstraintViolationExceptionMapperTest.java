package io.github.belgif.rest.problem.ee.jaxrs;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Response;

import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
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
class ConstraintViolationExceptionMapperTest {

    @Mock
    private ResourceInfo resourceInfo;

    @InjectMocks
    private ConstraintViolationExceptionMapper mapper;

    @Test
    void ok() {
        when(resourceInfo.getResourceClass()).thenReturn((Class) ProblemsEnabledResource.class);
        Response response = mapper.toResponse(new ConstraintViolationException(Collections.emptySet()));
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getEntity()).isInstanceOf(BadRequestProblem.class);
    }

    @Test
    void fallback() {
        when(resourceInfo.getResourceClass()).thenReturn((Class) ProblemsEnabledResource.class);
        Response response = mapper.toResponse(new ConstraintViolationException(Collections.emptySet()) {
            @Override
            public Set<ConstraintViolation<?>> getConstraintViolations() {
                throw new RuntimeException();
            }
        });
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getEntity()).isInstanceOf(InternalServerErrorProblem.class);
    }

    @Test
    void sortsIssuesByName() {
        when(resourceInfo.getResourceClass()).thenReturn((Class) ProblemsEnabledResource.class);
        Response response = mapper.toResponse(new ConstraintViolationException(new LinkedHashSet<>(
                Arrays.asList(
                        ConstraintViolationImpl.forParameterValidation(null, null, null, null, null, null, null,
                                null, PathImpl.createPathFromString("second"), null, null, null, null),
                        ConstraintViolationImpl.forParameterValidation(null, null, null, null, null, null, null,
                                null, PathImpl.createPathFromString("first"), null, null, null, null)))));
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getEntity()).isInstanceOf(BadRequestProblem.class);
        BadRequestProblem problem = (BadRequestProblem) response.getEntity();
        assertThat(problem.getIssues().get(0).getName()).isEqualTo("first");
        assertThat(problem.getIssues().get(1).getName()).isEqualTo("second");
    }

    @Test
    void disabled() {
        when(resourceInfo.getResourceClass()).thenReturn((Class) ProblemsDisabledResource.class);
        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> mapper.toResponse(new ConstraintViolationException(Collections.emptySet())));
    }

}
