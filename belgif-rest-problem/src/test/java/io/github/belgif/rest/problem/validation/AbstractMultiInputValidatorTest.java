package io.github.belgif.rest.problem.validation;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;

class AbstractMultiInputValidatorTest {

    static class MultiInputValidator extends AbstractMultiInputValidator<String> {
        MultiInputValidator(List<Input<? extends String>> inputs) {
            super(inputs);
        }

        @Override
        public Optional<InputValidationIssue> validate() {
            return Optional.empty();
        }
    }

    @Test
    void nullInputList() {
        assertThatNullPointerException()
                .isThrownBy(() -> new MultiInputValidator(null))
                .withMessage("input list should not be null");
    }

    @Test
    void emptyInputList() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new MultiInputValidator(Collections.emptyList()))
                .withMessage("input list can not be empty");
    }

    @Test
    void nullInput() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new MultiInputValidator(Collections.singletonList(null)))
                .withMessage("inputs can not be null");
    }

    @Test
    void nullInputIn() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new MultiInputValidator(Arrays.asList(new Input<>(null, "test", "value"))))
                .withMessage("inputs to validate must at least have a location and a name");
    }

    @Test
    void nullInputName() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new MultiInputValidator(Arrays.asList(new Input<>(InEnum.QUERY, null, "value"))))
                .withMessage("inputs to validate must at least have a location and a name");
    }

    @Test
    void getters() {
        Input<String> a = Input.query("a", "x");
        Input<String> b = Input.query("b", "y");
        MultiInputValidator validator = new MultiInputValidator(Arrays.asList(a, b));
        assertThat(validator.getInputs()).containsExactly(a, b);
        assertThat(validator.getInputValueStream()).containsExactly("x", "y");
    }

}
