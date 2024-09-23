package io.github.belgif.rest.problem.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
 * @see BadRequestProblem
 * @see InputValidationIssue
 * @see InputValidator
 *
 * @param <V> self-referencing AbstractRequestValidator type (for extensible builder pattern)
 */
public abstract class AbstractRequestValidator<V extends AbstractRequestValidator<V>>
        implements RequestValidatorModule<V> {

    private final List<InputValidator> validators = new ArrayList<>();

    /**
     * Perform all configured validations.
     *
     * @throws BadRequestProblem if the validation fails, containing each encountered {@link InputValidationIssue}
     */
    public void validate() throws BadRequestProblem {
        List<InputValidationIssue> issues = validators.stream()
                .map(InputValidator::validate)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        if (!issues.isEmpty()) {
            throw new BadRequestProblem(issues);
        }
    }

    public void addValidator(InputValidator validator) {
        this.validators.add(validator);
    }

}
