package io.github.belgif.rest.problem.validation;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class AnyOfValidatorTest {

    @Test
    void ok() {
        assertThat(new AnyOfValidator(Arrays.asList(Input.query("a", "value"), Input.query("b", null)))
                .validate()).isEmpty();
    }

    @Test
    void nok() {
        List<Input<?>> inputs = Arrays.asList(Input.query("a", null), Input.query("b", null));
        assertThat(new AnyOfValidator(inputs).validate()).contains(InputValidationIssues.anyOfExpected(inputs));
    }

}
