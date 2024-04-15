package io.github.belgif.rest.problem.validation;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class AnyOfValidatorTest {
    private AnyOfValidator tested;

    @Test
    void validateOk() {
        tested = new AnyOfValidator(
                Arrays.asList(Input.header("id", "25"), Input.body("id1", null)));
        assertThat(tested.validate()).isEmpty();
    }

    @Test
    void validateNullList() {
        assertThrows(NullPointerException.class, () -> new AnyOfValidator(null), "Should have thrown exception");
    }

    @Test
    void validateNullInputs() {
        assertThrows(IllegalArgumentException.class, () -> new AnyOfValidator(Arrays.asList(null, null)),
                "Should have thrown exception");
    }

    @Test
    void validateEmptyInputs() {
        assertThrows(IllegalArgumentException.class, () -> new AnyOfValidator(Collections.emptyList()),
                "Should have thrown exception");
    }

    @Test
    void validateNOK() {
        List<Input<?>> items =
                Arrays.asList(Input.header("id", null), Input.body("id1", null));
        tested = new AnyOfValidator(items);
        assertThat(tested.validate()).contains(InputValidationIssues.anyOfExpected(items));
    }
}
