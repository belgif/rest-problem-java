package io.github.belgif.rest.problem.validation;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

/**
 * Validates an input against a list of allowed reference data codes.
 *
 * @param <T> the input type
 */
class RefDataCollectionValidator<T> extends AbstractSingleInputValidator<T> {

    private final Collection<T> allowedRefData;

    RefDataCollectionValidator(Input<T> input, Collection<T> allowedRefData) {
        super(input);
        this.allowedRefData = Objects.requireNonNull(allowedRefData, "allowedRefData should not be null");
        if (allowedRefData.isEmpty()) {
            throw new IllegalStateException("allowedRefData should not be empty");
        }
    }

    @Override
    public Optional<InputValidationIssue> validate() throws BadRequestProblem {
        if (getValue() != null && !allowedRefData.contains(getValue())) {
            return Optional.of(InputValidationIssues.referencedResourceNotFound(getIn(), getName(), getValue()));
        }
        return Optional.empty();
    }
}
