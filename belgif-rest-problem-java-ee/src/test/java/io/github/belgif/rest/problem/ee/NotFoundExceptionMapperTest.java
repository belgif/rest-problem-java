package io.github.belgif.rest.problem.ee;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;
import io.github.belgif.rest.problem.ee.jaxrs.NotFoundExceptionMapper;
import org.junit.jupiter.api.Test;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.*;

public class NotFoundExceptionMapperTest {

    @Test
    public void testParseLocation() {
        Response response = new NotFoundExceptionMapper().toResponse(new NotFoundException("RESTEASY003870: Unable to extract parameter from http request: jakarta.ws.rs.QueryParam(\"startDate_gt\") value is '2006-087-01'", new BadRequestProblem()));
        BadRequestProblem problem = (BadRequestProblem) response.getEntity();
        assertEquals(BadRequestProblem.TYPE_URI, problem.getType());
        assertEquals(BadRequestProblem.HREF, problem.getHref());

        assertFalse(problem.getIssues().isEmpty());
        InputValidationIssue issue = problem.getIssues().get(0);
        assertEquals(InEnum.QUERY, issue.getIn());
        assertEquals("startDate_gt", issue.getName());
    }

}
