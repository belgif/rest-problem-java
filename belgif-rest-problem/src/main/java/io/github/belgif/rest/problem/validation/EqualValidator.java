package io.github.belgif.rest.problem.validation;

import java.util.List;
import java.util.Optional;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

/**
 * Validates that the given inputs are equal (according to {@link Object#equals(Object)}).
 */
class EqualValidator extends AbstractMultiInputValidator<Object> {

    EqualValidator(List<Input<?>> inputs) {
        super(inputs);
    }

    @Override
    public Optional<InputValidationIssue> validate() throws BadRequestProblem {
        if (getInputValueStream().distinct().count() > 1) {
            return Optional.of(InputValidationIssues.equalExpected(getInputs()));
        }
        return Optional.empty();
    }
}
