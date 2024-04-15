package io.github.belgif.rest.problem.validation;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import io.github.belgif.rest.problem.api.Input;

/**
 * Abstract base class for {@link InputValidator} with multiple inputs.
 *
 * @param <V> the input value type
 */
public abstract class AbstractMultiInputValidator<V> implements InputValidator {

    private final List<Input<? extends V>> inputs;

    protected AbstractMultiInputValidator(List<Input<? extends V>> inputs) {
        Objects.requireNonNull(inputs);
        if (inputs.isEmpty()) {
            throw new IllegalArgumentException("input list can not be empty");
        }
        if (inputs.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("inputs can not be null");
        }
        if (inputs.stream().anyMatch(i -> i.getName() == null || i.getIn() == null)) {
            throw new IllegalArgumentException("inputs to validate must at least have a location and a name");
        }
        this.inputs = Collections.unmodifiableList(inputs);
    }

    protected List<Input<? extends V>> getInputs() {
        return inputs;
    }

    protected Stream<V> getInputValueStream() {
        return inputs.stream().map(Input::getValue);
    }

}
