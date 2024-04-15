package io.github.belgif.rest.problem.validation;

import java.util.Optional;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

/**
 * Validates that an input value is present.
 *
 * @param <T> the input type
 */
class RequiredInputValidator<T> extends AbstractSingleInputValidator<T> {

    RequiredInputValidator(Input<T> input) {
        super(input);
    }

    @Override
    public Optional<InputValidationIssue> validate() {
        if (getValue() == null) {
            return Optional.of(InputValidationIssues.requiredInput(getIn(), getName(), getValue()));
        } else {
            return Optional.empty();
        }
    }

}
