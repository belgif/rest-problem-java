package io.github.belgif.rest.problem.validation;

import java.time.temporal.Temporal;
import java.util.Objects;
import java.util.Optional;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

/**
 * Validates that two comparable temporal inputs make a valid period.
 *
 * @param <T> the comparable temporal type
 */
class PeriodMultiInputValidator<T extends Temporal & Comparable<? super T>> implements InputValidator {

    private final Input<T> start;

    private final Input<T> end;

    PeriodMultiInputValidator(Input<T> start, Input<T> end) {
        this.start = Objects.requireNonNull(start, "start should not be null");
        this.end = Objects.requireNonNull(end, "end should not be null");
    }

    @Override
    public Optional<InputValidationIssue> validate() {
        if (start.getValue() != null && end.getValue() != null && end.getValue().compareTo(start.getValue()) < 0) {
            return Optional.of(InputValidationIssues.invalidPeriod(start, end));
        }
        return Optional.empty();
    }

}
