package io.github.belgif.rest.problem.validation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

/**
 * Validates that any of the given inputs is present.
 */
class AnyOfValidator extends AbstractMultiInputValidator<Object> {

    AnyOfValidator(List<Input<?>> inputs) {
        super(inputs);
    }

    @Override
    public Optional<InputValidationIssue> validate() {
        if (getInputValueStream().noneMatch(Objects::nonNull)) {
            return Optional.of(InputValidationIssues.anyOfExpected(getInputs()));
        }
        return Optional.empty();
    }
}
