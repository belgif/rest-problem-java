package io.github.belgif.rest.problem.ee.jaxrs;

import static org.assertj.core.api.Assertions.*;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.ResourceNotFoundProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class NotFoundExceptionMapperTest {

    private final NotFoundExceptionMapper mapper = new NotFoundExceptionMapper();

    @Test
    void notFoundException() {
        Response response = mapper.toResponse(new NotFoundException());
        assertThat(response.getEntity()).isInstanceOf(ResourceNotFoundProblem.class);
    }

    @Test
    void badRequestProblem() {
        BadRequestProblem cause = new BadRequestProblem(
                InputValidationIssues.schemaViolation(InEnum.QUERY, "startDate_gt", "2006-087-01",
                        "date has invalid format"));
        Response response = mapper.toResponse(new NotFoundException("HTTP 404 Not Found", cause));
        assertThat(response.getEntity()).isInstanceOf(BadRequestProblem.class);
        BadRequestProblem problem = (BadRequestProblem) response.getEntity();
        InputValidationIssue issue = problem.getIssues().get(0);
        assertThat(issue.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(issue.getName()).isEqualTo("startDate_gt");
    }

    @Test
    void badRequestProblemEnrichFromMessage() {
        BadRequestProblem cause = new BadRequestProblem(
                InputValidationIssues.schemaViolation(null, null, "2006-087-01", "date has invalid format"));
        Response response = mapper.toResponse(new NotFoundException(
                "RESTEASY003870: Unable to extract parameter from http request: "
                        + "jakarta.ws.rs.QueryParam(\"startDate_gt\") value is '2006-087-01'",
                cause));
        assertThat(response.getEntity()).isInstanceOf(BadRequestProblem.class);
        BadRequestProblem problem = (BadRequestProblem) response.getEntity();
        InputValidationIssue issue = problem.getIssues().get(0);
        assertThat(issue.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(issue.getName()).isEqualTo("startDate_gt");
    }

    @Test
    void badRequestProblemNotEnrichableFromMessage() {
        BadRequestProblem cause = new BadRequestProblem(
                InputValidationIssues.schemaViolation(null, null, "2006-087-01", "date has invalid format"));
        Response response = mapper.toResponse(new NotFoundException("HTTP 404 Not Found", cause));
        assertThat(response.getEntity()).isInstanceOf(BadRequestProblem.class);
        BadRequestProblem problem = (BadRequestProblem) response.getEntity();
        InputValidationIssue issue = problem.getIssues().get(0);
        assertThat(issue.getIn()).isNull();
        assertThat(issue.getName()).isNull();
    }

}
