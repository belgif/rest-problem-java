package io.github.belgif.rest.problem.ee.jaxrs;

import static org.assertj.core.api.Assertions.*;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.fasterxml.jackson.databind.type.SimpleType;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssue;

class JacksonJsonMappingExceptionMapperTest {

    private final JacksonJsonMappingExceptionMapper mapper = new JacksonJsonMappingExceptionMapper();

    @Test
    void toResponse() {
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

    enum Size {
        S, M, L
    }

    @Test
    void valueInstantiationExceptionToResponse() {
        ValueInstantiationException exception = ValueInstantiationException.from(null,
                "Cannot construct instance of `io.github.belgif.rest.problem.ee.jaxrs.JacksonJsonMappingExceptionMapperTest.Size`, problem: Unexpected value 'XXL'",
                SimpleType.constructUnsafe(Size.class));
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
        assertThat(issue.getDetail()).isEqualTo("Unexpected value 'XXL'");
    }

}
