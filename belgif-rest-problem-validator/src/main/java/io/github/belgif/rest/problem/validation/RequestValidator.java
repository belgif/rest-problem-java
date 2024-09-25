package io.github.belgif.rest.problem.validation;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InputValidationIssue;

/**
 * Performs validation on input parameters of an API request.
 *
 * <p>
 * This validation does not stop on the first invalid input. It performs all configured validations and if any of
 * them failed, a {@link BadRequestProblem} is thrown, containing each encountered {@link InputValidationIssue}.
 * </p>
 *
 * <p>
 * If you wish to create a custom RequestValidator implementation, please extend from {@link AbstractRequestValidator}.
 * </p>
 *
 * @see BadRequestProblem
 * @see InputValidationIssue
 * @see InputValidator
 */
public final class RequestValidator extends AbstractRequestValidator<RequestValidator> {

}
