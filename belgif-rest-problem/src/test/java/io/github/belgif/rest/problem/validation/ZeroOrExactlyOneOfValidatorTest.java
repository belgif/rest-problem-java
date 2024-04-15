package io.github.belgif.rest.problem.validation;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class ZeroOrExactlyOneOfValidatorTest {
    private ZeroOrExactlyOneOfValidator tested;

    @Test
    void validateOkZero() {
        tested = new ZeroOrExactlyOneOfValidator(
                Arrays.asList(Input.body("cbeNumber", null), Input.body("sector", null)));
        assertThat(tested.validate()).isEmpty();
    }

    @Test
    void validateOkOne() {
        tested = new ZeroOrExactlyOneOfValidator(
                Arrays.asList(Input.body("cbeNumber", null), Input.body("sector", "25")));
        assertThat(tested.validate()).isEmpty();
    }

    @Test
    void validateNullList() {
        assertThrows(NullPointerException.class, () -> new ZeroOrExactlyOneOfValidator(null),
                "Should have thrown exception");
    }

    @Test
    void validateNullInputs() {
        assertThrows(IllegalArgumentException.class, () -> new ZeroOrExactlyOneOfValidator(Arrays.asList(null, null)),
                "Should have thrown exception");
    }

    @Test
    void validateEmptyInputs() {
        assertThrows(IllegalArgumentException.class, () -> new ZeroOrExactlyOneOfValidator(Collections.emptyList()),
                "Should have thrown exception");
    }

    @Test
    void validateNOk() {
        List<Input<?>> items = Arrays.asList(Input.body("cbeNumber", "0694965804"),
                Input.body("sector", "25"));
        tested = new ZeroOrExactlyOneOfValidator(items);
        InputValidationIssue expected = InputValidationIssues.zeroOrExactlyOneOfExpected(items);
        assertThat(tested.validate()).contains(expected);
    }
}
