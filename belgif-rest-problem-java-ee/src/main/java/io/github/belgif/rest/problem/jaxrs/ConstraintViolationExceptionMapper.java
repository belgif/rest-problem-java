package io.github.belgif.rest.problem.jaxrs;

import java.util.Comparator;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.internal.ConstraintViolationUtil;

/**
 * ExceptionMapper for mapping ConstraintViolationException to BadRequestProblem.
 *
 * @see ExceptionMapper
 * @see ConstraintViolationException
 * @see BadRequestProblem
 */
@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    private static final DefaultExceptionMapper DEFAULT_MAPPER = new DefaultExceptionMapper();

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        try {
            return ProblemMediaType.INSTANCE.toResponse(new BadRequestProblem(
                    exception.getConstraintViolations().stream()
                            .map(ConstraintViolationUtil::convertToInputValidationIssue)
                            .sorted(Comparator.comparing(InputValidationIssue::getName))
                            .collect(Collectors.toList())));
        } catch (RuntimeException e) {
            return DEFAULT_MAPPER.toResponse(e);
        }
    }

}
