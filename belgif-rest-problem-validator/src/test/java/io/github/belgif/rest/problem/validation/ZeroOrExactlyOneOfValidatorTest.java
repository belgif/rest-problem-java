package io.github.belgif.rest.problem.validation;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class ZeroOrExactlyOneOfValidatorTest {

    @Test
    void okZero() {
        assertThat(new ZeroOrExactlyOneOfValidator(
                Arrays.asList(Input.body("cbeNumber", null), Input.body("sector", null)))
                        .validate()).isEmpty();
    }

    @Test
    void okOne() {
        assertThat(new ZeroOrExactlyOneOfValidator(
                Arrays.asList(Input.body("cbeNumber", null), Input.body("sector", "25")))
                        .validate()).isEmpty();
    }

    @Test
    void validateNOk() {
        List<Input<?>> items = Arrays.asList(Input.body("cbeNumber", "0694965804"),
                Input.body("sector", "25"));
        assertThat(new ZeroOrExactlyOneOfValidator(items).validate()).contains(
                InputValidationIssues.zeroOrExactlyOneOfExpected(items));
    }
}
