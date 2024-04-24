package io.github.belgif.rest.problem.validation;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

/**
 * Validates an input against a list of allowed reference data codes.
 *
 * @param <T> the input type
 */
class RefDataPredicateValidator<T> extends AbstractSingleInputValidator<T> {

    private final Predicate<T> allowedRefDataPredicate;

    RefDataPredicateValidator(Input<T> input, Predicate<T> allowedRefDataPredicate) {
        super(input);
        this.allowedRefDataPredicate =
                Objects.requireNonNull(allowedRefDataPredicate, "allowedRefData predicate should not be null");
    }

    @Override
    public Optional<InputValidationIssue> validate() throws BadRequestProblem {
        if (getValue() != null && !allowedRefDataPredicate.test(getValue())) {
            return Optional.of(InputValidationIssues.referencedResourceNotFound(getIn(), getName(), getValue()));
        }
        return Optional.empty();
    }
}
