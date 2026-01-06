package io.github.belgif.rest.problem.ee.jaxrs;

import static org.assertj.core.api.Assertions.*;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonParseException;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssue;

class JacksonJsonParseExceptionMapperTest {

    private final JacksonJsonParseExceptionMapper mapper = new JacksonJsonParseExceptionMapper();

    @Test
    void toResponse() {
        JsonParseException exception = new JsonParseException(null, "detail");
        Response response = mapper.toResponse(exception);
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getEntity()).isInstanceOf(BadRequestProblem.class);
        BadRequestProblem problem = (BadRequestProblem) response.getEntity();
        InputValidationIssue issue = problem.getIssues().get(0);
        assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:schemaViolation");
        assertThat(issue.getIn()).isEqualTo(InEnum.BODY);
        assertThat(issue.getName()).isNull();
        assertThat(issue.getValue()).isNull();
        assertThat(issue.getDetail()).isEqualTo("JSON syntax error");
    }

}
