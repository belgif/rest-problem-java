package io.github.belgif.rest.problem.validation;

import java.util.Optional;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

/**
 * Validates that the given input value is in the given [min, max] range.
 *
 * @param <T> the input type
 */
class RangeValidator<T extends Comparable<T>> extends AbstractSingleInputValidator<T> {

    private final T min;

    private final T max;

    RangeValidator(Input<T> input, T min, T max) {
        super(input);
        if (min == null && max == null) {
            throw new IllegalArgumentException("At least one of min, max must be non-null");
        }
        this.min = min;
        this.max = max;
    }

    @Override
    public Optional<InputValidationIssue> validate() {
        T value = getValue();
        if ((min != null && value.compareTo(min) < 0) || (max != null && value.compareTo(max) > 0)) {
            return Optional.of(InputValidationIssues.outOfRange(getIn(), getName(), getValue(), min, max));
        }
        return Optional.empty();
    }

}
