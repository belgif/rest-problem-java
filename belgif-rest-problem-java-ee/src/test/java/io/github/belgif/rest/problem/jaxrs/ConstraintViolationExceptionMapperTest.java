package io.github.belgif.rest.problem.jaxrs;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;

import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.InternalServerErrorProblem;

class ConstraintViolationExceptionMapperTest {

    private final ConstraintViolationExceptionMapper mapper = new ConstraintViolationExceptionMapper();

    @Test
    void ok() {
        Response response = mapper.toResponse(new ConstraintViolationException(Collections.emptySet()));
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getEntity()).isInstanceOf(BadRequestProblem.class);
    }

    @Test
    void fallback() {
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

}
