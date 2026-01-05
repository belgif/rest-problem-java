package io.github.belgif.rest.problem.ee.jaxrs;

import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.internal.JacksonUtil;

/**
 * ExceptionMapper for mapping jackson MismatchedInputException to BadRequestProblem.
 *
 * @see MismatchedInputException
 * @see BadRequestProblem
 */
@Provider
public class JacksonMismatchedInputExceptionMapper extends AbstractProblemExceptionMapper<MismatchedInputException> {

    public JacksonMismatchedInputExceptionMapper() {
        super(MismatchedInputException.class);
    }

    @Override
    public Problem toProblem(MismatchedInputException exception) {
        return JacksonUtil.toBadRequestProblem(exception);
    }

}
