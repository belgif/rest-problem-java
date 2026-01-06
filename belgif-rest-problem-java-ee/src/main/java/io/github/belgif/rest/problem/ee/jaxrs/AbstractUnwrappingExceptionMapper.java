package io.github.belgif.rest.problem.ee.jaxrs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.ee.internal.ParameterSourceMapper;

public abstract class AbstractUnwrappingExceptionMapper<T extends WebApplicationException>
        implements ExceptionMapper<T> {

    private static final Pattern PARAM_PATTERN =
            Pattern.compile("(javax|jakarta).ws.rs.((?:Path|Query|Header|Bean|Form|Cookie|Matrix)Param)\\(\"(.*)\"\\)");

    @Override
    public Response toResponse(T exception) {
        if (exception.getCause() instanceof BadRequestProblem) {
            BadRequestProblem problem = (BadRequestProblem) exception.getCause();
            if (!problem.getIssues().isEmpty()) {
                InputValidationIssue issue = problem.getIssues().get(0);
                if (issue.getIn() == null && issue.getName() == null) {
                    // best-effort to enrich the InputValidationIssue based on the exception message
                    Matcher matcher = PARAM_PATTERN.matcher(exception.getMessage());
                    if (matcher.find()) {
                        issue.in(ParameterSourceMapper.map(matcher.group(2)));
                        issue.name(matcher.group(3));
                    }
                }
            }
            return ProblemMediaType.INSTANCE.toResponse(problem);
        } else {
            return toDefaultResponse(exception);
        }
    }

    protected abstract Response toDefaultResponse(T exception);

}
