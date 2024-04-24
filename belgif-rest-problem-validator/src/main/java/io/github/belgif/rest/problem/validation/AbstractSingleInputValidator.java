package io.github.belgif.rest.problem.validation;

import java.util.Objects;

import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.Input;

/**
 * Abstract base class for {@link InputValidator} with a single input.
 *
 * @param <V> the input value type
 */
public abstract class AbstractSingleInputValidator<V> implements InputValidator {

    private final Input<? extends V> input;

    protected AbstractSingleInputValidator(Input<? extends V> input) {
        Objects.requireNonNull(input, "input should not be null");
        Objects.requireNonNull(input.getIn(), "in should not be null");
        Objects.requireNonNull(input.getName(), "name should not be null");
        this.input = input;
    }

    protected Input<? extends V> getInput() {
        return input;
    }

    protected InEnum getIn() {
        return input.getIn();
    }

    protected String getName() {
        return input.getName();
    }

    protected V getValue() {
        return input.getValue();
    }

}
