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
 * <p>
 * If your custom RequestValidator implementation needs to be extensible as well, you can propagate the extensible
 * fluent builder pattern as follows:
 * </p>
 *
 * <pre>{@code
 * public abstract class AbstractMyRequestValidator<SELF extends AbstractMyRequestValidator<SELF>>
 *         extends AbstractRequestValidator<SELF> {
 *
 *     public SELF something(Input<String> input) {
 *         addValidator(new SomethingValidator(input));
 *         return getThis();
 *     }
 *
 * }
 *
 * public final class MyRequestValidator extends AbstractMyRequestValidator<MyRequestValidator> {
 *
 * }
 * }</pre>
 *
 * @see BadRequestProblem
 * @see InputValidationIssue
 * @see InputValidator
 */
public final class RequestValidator extends AbstractRequestValidator<RequestValidator> {

}
