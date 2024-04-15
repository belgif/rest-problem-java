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

class EqualValidatorTest {
    private EqualValidator tested;

    @Test
    void validateOk() {
        tested = new EqualValidator(
                Arrays.asList(Input.header("id", "25"), Input.body("id", "25")));
        assertThat(tested.validate()).isEmpty();
    }

    @Test
    void validateNullList() {
        assertThrows(NullPointerException.class, () -> new EqualValidator(null),
                "Should have thrown exception");
    }

    @Test
    void validateNullInputs() {
        assertThrows(IllegalArgumentException.class, () -> new EqualValidator(Arrays.asList(null, null)),
                "Should have thrown exception");
    }

    @Test
    void validateEmptyInputs() {
        assertThrows(IllegalArgumentException.class, () -> new EqualValidator(Collections.emptyList()),
                "Should have thrown exception");
    }

    @Test
    void validateNOk() {
        List<Input<?>> items = Arrays.asList(Input.header("id", "25"),
                Input.body("id", "26"));
        tested = new EqualValidator(items);
        InputValidationIssue expected = InputValidationIssues.equalExpected(items);
        assertThat(tested.validate()).contains(expected);
    }
}
