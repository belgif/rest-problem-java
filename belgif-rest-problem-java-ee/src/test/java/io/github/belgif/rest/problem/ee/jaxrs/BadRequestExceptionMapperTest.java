package io.github.belgif.rest.problem.ee.jaxrs;

import static org.assertj.core.api.Assertions.*;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class BadRequestExceptionMapperTest {

    private final BadRequestExceptionMapper mapper = new BadRequestExceptionMapper();

    @Test
    void badRequestException() {
        Response response = mapper.toResponse(new BadRequestException());
        assertThat(response.getEntity()).isInstanceOf(BadRequestProblem.class);
    }

    @Test
    void badRequestProblem() {
        BadRequestProblem cause = new BadRequestProblem(
                InputValidationIssues.schemaViolation(InEnum.HEADER, "startDate_gt", "2006-087-01",
                        "date has invalid format"));
        Response response = mapper.toResponse(new BadRequestException("HTTP 400 Bad Request", cause));
        assertThat(response.getEntity()).isInstanceOf(BadRequestProblem.class);
        BadRequestProblem problem = (BadRequestProblem) response.getEntity();
        InputValidationIssue issue = problem.getIssues().get(0);
        assertThat(issue.getIn()).isEqualTo(InEnum.HEADER);
        assertThat(issue.getName()).isEqualTo("startDate_gt");
    }

    @Test
    void badRequestProblemEnrichFromMessage() {
        BadRequestProblem cause = new BadRequestProblem(
                InputValidationIssues.schemaViolation(null, null, "2006-087-01", "date has invalid format"));
        Response response = mapper.toResponse(new BadRequestException(
                "RESTEASY003870: Unable to extract parameter from http request: "
                        + "jakarta.ws.rs.HeaderParam(\"startDate_gt\") value is '2006-087-01'",
                cause));
        assertThat(response.getEntity()).isInstanceOf(BadRequestProblem.class);
        BadRequestProblem problem = (BadRequestProblem) response.getEntity();
        InputValidationIssue issue = problem.getIssues().get(0);
        assertThat(issue.getIn()).isEqualTo(InEnum.HEADER);
        assertThat(issue.getName()).isEqualTo("startDate_gt");
    }

    @Test
    void badRequestProblemNotEnrichableFromMessage() {
        BadRequestProblem cause = new BadRequestProblem(
                InputValidationIssues.schemaViolation(null, null, "2006-087-01", "date has invalid format"));
        Response response = mapper.toResponse(new BadRequestException("HTTP 400 Bad Request", cause));
        assertThat(response.getEntity()).isInstanceOf(BadRequestProblem.class);
        BadRequestProblem problem = (BadRequestProblem) response.getEntity();
        InputValidationIssue issue = problem.getIssues().get(0);
        assertThat(issue.getIn()).isNull();
        assertThat(issue.getName()).isNull();
    }

}
