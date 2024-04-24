package io.github.belgif.rest.problem.validation;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class EqualValidatorTest {

    @Test
    void ok() {
        assertThat(new EqualValidator(Arrays.asList(Input.header("id", "25"), Input.body("id", "25")))
                .validate()).isEmpty();
    }

    @Test
    void okNull() {
        assertThat(new EqualValidator(Arrays.asList(Input.header("id", null), Input.body("id", null)))
                .validate()).isEmpty();
    }

    @Test
    void nok() {
        List<Input<?>> items = Arrays.asList(Input.header("id", "25"), Input.body("id", "26"));
        assertThat(new EqualValidator(items).validate()).contains(InputValidationIssues.equalExpected(items));
    }

}
