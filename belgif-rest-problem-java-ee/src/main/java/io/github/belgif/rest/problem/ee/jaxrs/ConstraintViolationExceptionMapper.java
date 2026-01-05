package io.github.belgif.rest.problem.ee.jaxrs;

import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.ee.internal.ConstraintViolationUtil;

/**
 * ExceptionMapper for mapping ConstraintViolationException to BadRequestProblem.
 *
 * @see ExceptionMapper
 * @see ConstraintViolationException
 * @see BadRequestProblem
 */
@Provider
public class ConstraintViolationExceptionMapper extends AbstractProblemExceptionMapper<ConstraintViolationException> {

    public ConstraintViolationExceptionMapper() {
        super(ConstraintViolationException.class);
    }

    @Override
    protected Problem toProblem(ConstraintViolationException exception) {
        return new BadRequestProblem(
                exception.getConstraintViolations().stream()
                        .map(ConstraintViolationUtil::convertToInputValidationIssue)
                        .sorted(InputValidationIssue.BY_NAME)
                        .collect(Collectors.toList()));
    }

}
