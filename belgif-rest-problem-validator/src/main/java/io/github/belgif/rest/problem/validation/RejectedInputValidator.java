package io.github.belgif.rest.problem.validation;

import java.util.Objects;
import java.util.Optional;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

/**
 * Validates that an input value is not present.
 *
 * @param <T> the input type
 */
class RejectedInputValidator<T> extends AbstractSingleInputValidator<T> {

    RejectedInputValidator(Input<T> input) {
        super(input);
    }

    @Override
    public Optional<InputValidationIssue> validate() {
        if (Objects.nonNull(getValue())) {
            return Optional.of(InputValidationIssues.rejectedInput(getIn(), getName(), getValue()));
        } else {
            return Optional.empty();
        }
    }

}
