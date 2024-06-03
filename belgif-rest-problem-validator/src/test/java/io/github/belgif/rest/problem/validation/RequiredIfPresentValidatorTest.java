package io.github.belgif.rest.problem.validation;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class RequiredIfPresentValidatorTest {

    @Test
    void okTargetNotPresent() {
        assertThat(new RequiredIfPresentValidator(Input.query("x", null),
                Arrays.asList(Input.query("a", "value"), Input.query("b", null)))
                        .validate()).isEmpty();
    }

    @Test
    void okTargetPresent() {
        assertThat(new RequiredIfPresentValidator(Input.query("x", "value"),
                Arrays.asList(Input.query("a", "value"), Input.query("b", "ok")))
                        .validate()).isEmpty();
    }

    @Test
    void nok() {
        Input<?> target = Input.query("x", "value");
        List<Input<?>> inputs = Arrays.asList(Input.query("a", "value"), Input.query("b", null));
        assertThat(new RequiredIfPresentValidator(target, inputs).validate()).contains(
                InputValidationIssues.requiredInputsIfPresent(target, inputs));
    }

}
