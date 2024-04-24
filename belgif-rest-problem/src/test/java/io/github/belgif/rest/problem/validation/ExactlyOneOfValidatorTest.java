package io.github.belgif.rest.problem.validation;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class ExactlyOneOfValidatorTest {

    @Test
    void ok() {
        assertThat(new ExactlyOneOfValidator(Arrays.asList(Input.body("cbeNumber", null), Input.body("sector", "25")))
                .validate()).isEmpty();
    }

    @Test
    void nokMoreThanOne() {
        List<Input<?>> items = Arrays.asList(Input.body("cbeNumber", "0694965804"),
                Input.body("sector", "25"));
        assertThat(new ExactlyOneOfValidator(items).validate())
                .contains(InputValidationIssues.exactlyOneOfExpected(items));
    }

    @Test
    void nokNone() {
        List<Input<?>> items = Arrays.asList(Input.body("cbeNumber", null),
                Input.body("sector", null));
        assertThat(new ExactlyOneOfValidator(items).validate())
                .contains(InputValidationIssues.exactlyOneOfExpected(items));
    }

}
