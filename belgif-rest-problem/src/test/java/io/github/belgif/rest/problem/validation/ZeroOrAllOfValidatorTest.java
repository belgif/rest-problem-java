package io.github.belgif.rest.problem.validation;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class ZeroOrAllOfValidatorTest {
    private ZeroOrAllOfValidator tested;

    @Test
    void validateOkZero() {
        tested = new ZeroOrAllOfValidator(
                Arrays.asList(Input.body("cbeNumber", null), Input.body("sector", null)));
        assertThat(tested.validate()).isEmpty();
    }

    @Test
    void validateOkAll() {
        tested = new ZeroOrAllOfValidator(
                Arrays.asList(Input.body("cbeNumber", "25"), Input.body("sector", "25")));
        assertThat(tested.validate()).isEmpty();
    }

    @Test
    void validateNullList() {
        assertThrows(NullPointerException.class, () -> new ZeroOrAllOfValidator(null),
                "Should have thrown exception");
    }

    @Test
    void validateNullInputs() {
        assertThrows(IllegalArgumentException.class, () -> new ZeroOrAllOfValidator(Arrays.asList(null, null)),
                "Should have thrown exception");
    }

    @Test
    void validateEmptyInputs() {
        assertThrows(IllegalArgumentException.class, () -> new ZeroOrAllOfValidator(Collections.emptyList()),
                "Should have thrown exception");
    }

    @Test
    void validateNOk() {
        List<Input<?>> items = Arrays.asList(Input.body("cbeNumber", "0694965804"),
                Input.body("sector", null));
        tested = new ZeroOrAllOfValidator(items);
        InputValidationIssue expected = InputValidationIssues.zeroOrAllOfExpected(items);
        assertThat(tested.validate()).contains(expected);
    }
}
