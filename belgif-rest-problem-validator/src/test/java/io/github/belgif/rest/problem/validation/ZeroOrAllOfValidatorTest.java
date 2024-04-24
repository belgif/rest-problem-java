package io.github.belgif.rest.problem.validation;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class ZeroOrAllOfValidatorTest {

    @Test
    void okZero() {
        assertThat(new ZeroOrAllOfValidator(Arrays.asList(Input.body("cbeNumber", null), Input.body("sector", null)))
                .validate()).isEmpty();
    }

    @Test
    void okAll() {
        assertThat(new ZeroOrAllOfValidator(Arrays.asList(Input.body("cbeNumber", "25"), Input.body("sector", "25")))
                .validate()).isEmpty();
    }

    @Test
    void nok() {
        List<Input<?>> items = Arrays.asList(Input.body("cbeNumber", "0694965804"),
                Input.body("sector", null));
        assertThat(new ZeroOrAllOfValidator(items).validate()).contains(
                InputValidationIssues.zeroOrAllOfExpected(items));
    }

}
