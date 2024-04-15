package io.github.belgif.rest.problem.validation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

/**
 * Validates that exactly one of the given inputs is present.
 */
class ExactlyOneOfValidator extends AbstractMultiInputValidator<Object> {

    ExactlyOneOfValidator(List<Input<?>> inputs) {
        super(inputs);
    }

    @Override
    public Optional<InputValidationIssue> validate() {
        if (getInputValueStream().filter(Objects::nonNull).count() != 1) {
            return Optional.of(InputValidationIssues.exactlyOneOfExpected(getInputs()));
        }
        return Optional.empty();
    }
}
