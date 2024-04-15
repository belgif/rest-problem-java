package io.github.belgif.rest.problem.validation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

/**
 * Validates that all or none of the given inputs are present.
 */
class ZeroOrAllOfValidator extends AbstractMultiInputValidator<Object> {

    ZeroOrAllOfValidator(List<Input<?>> inputs) {
        super(inputs);
    }

    @Override
    public Optional<InputValidationIssue> validate() {
        if (!getInputValueStream().allMatch(Objects::nonNull)
                && !getInputValueStream().allMatch(Objects::isNull)) {
            return Optional.of(InputValidationIssues.zeroOrAllOfExpected(getInputs()));
        }
        return Optional.empty();
    }
}
