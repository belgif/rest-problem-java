package io.github.belgif.rest.problem.ee.jaxrs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.ResourceNotFoundProblem;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.ee.internal.ParameterSourceMapper;

/**
 * {@link javax.ws.rs.ext.ParamConverter} errors are wrapped into a {@link javax.ws.rs.NotFoundException} in JBoss EAP.
 * This ExceptionMapper unwraps them into 400 Bad Request.
 */
@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotFoundExceptionMapper.class);

    private static final Pattern PARAM_PATTERN =
            Pattern.compile("(javax|jakarta).ws.rs.((?:Path|Query|Header|Bean|Form|Cookie|Matrix)Param)\\(\"(.*)\"\\)");

    @Override
    public Response toResponse(NotFoundException exception) {
        LOGGER.warn(exception.getMessage(), exception);
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
            return ProblemMediaType.INSTANCE.toResponse(new ResourceNotFoundProblem());
        }
    }

}
