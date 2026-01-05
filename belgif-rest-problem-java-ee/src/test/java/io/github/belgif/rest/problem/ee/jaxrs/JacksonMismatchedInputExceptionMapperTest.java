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

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.ee.jaxrs.AbstractProblemExceptionMapperTest.ProblemsDisabledResource;
import io.github.belgif.rest.problem.ee.jaxrs.AbstractProblemExceptionMapperTest.ProblemsEnabledResource;

@ExtendWith(MockitoExtension.class)
class JacksonMismatchedInputExceptionMapperTest {

    @Mock
    private ResourceInfo resourceInfo;

    @InjectMocks
    private JacksonMismatchedInputExceptionMapper mapper;

    @Test
    void toResponse() {
        when(resourceInfo.getResourceClass()).thenReturn((Class) ProblemsEnabledResource.class);
        MismatchedInputException exception = MismatchedInputException.from(null, Object.class, "detail");
        exception.prependPath(new JsonMappingException.Reference(null, "id"));
        Response response = mapper.toResponse(exception);
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getEntity()).isInstanceOf(BadRequestProblem.class);
        BadRequestProblem problem = (BadRequestProblem) response.getEntity();
        InputValidationIssue issue = problem.getIssues().get(0);
        assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:schemaViolation");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isEqualTo("id");
        assertThat(issue.getValue()).isNull();
        assertThat(issue.getDetail()).isEqualTo("detail");
    }

    @Test
    void disabled() {
        when(resourceInfo.getResourceClass()).thenReturn((Class) ProblemsDisabledResource.class);
        MismatchedInputException exception = MismatchedInputException.from(null, Object.class, "detail");
        assertThatException().isThrownBy(() -> mapper.toResponse(exception)).isEqualTo(exception);
    }

}
