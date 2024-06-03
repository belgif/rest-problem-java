package io.github.belgif.rest.problem.validation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

/**
 * Validate that all the given inputs are present when the given target input is present.
 */
class RequiredIfPresentValidator extends AbstractMultiInputValidator<Object> {

    private final Input<?> target;

    RequiredIfPresentValidator(Input<?> target, List<Input<?>> inputs) {
        super(inputs);
        this.target = target;
    }

    @Override
    public Optional<InputValidationIssue> validate() {
        if (target.getValue() != null && getInputValueStream().anyMatch(Objects::isNull)) {
            return Optional.of(InputValidationIssues.requiredInputsIfPresent(target, getInputs()));
        }
        return Optional.empty();
    }

}
