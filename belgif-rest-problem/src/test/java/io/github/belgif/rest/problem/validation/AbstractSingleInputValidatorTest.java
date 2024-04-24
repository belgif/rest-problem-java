package io.github.belgif.rest.problem.validation;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;

class AbstractSingleInputValidatorTest {

    static class SingleInputValidator extends AbstractSingleInputValidator<String> {
        SingleInputValidator(Input<String> input) {
            super(input);
        }

        @Override
        public Optional<InputValidationIssue> validate() {
            return Optional.empty();
        }
    }

    @Test
    void nullInput() {
        assertThatNullPointerException()
                .isThrownBy(() -> new SingleInputValidator(null))
                .withMessage("input should not be null");
    }

    @Test
    void nullInputIn() {
        assertThatNullPointerException()
                .isThrownBy(() -> new SingleInputValidator(new Input<>(null, "test", "value")))
                .withMessage("in should not be null");
    }

    @Test
    void nullInputName() {
        assertThatNullPointerException()
                .isThrownBy(() -> new SingleInputValidator(new Input<>(InEnum.QUERY, null, "value")))
                .withMessage("name should not be null");
    }

    @Test
    void getters() {
        SingleInputValidator validator = new SingleInputValidator(Input.query("test", "value"));
        assertThat(validator.getInput()).isEqualTo(Input.query("test", "value"));
        assertThat(validator.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(validator.getName()).isEqualTo("test");
        assertThat(validator.getValue()).isEqualTo("value");
    }

}
