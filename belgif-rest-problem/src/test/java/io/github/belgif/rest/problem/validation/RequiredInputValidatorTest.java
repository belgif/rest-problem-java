package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class RequiredInputValidatorTest {

    @Test
    void ok() {
        assertThat(new RequiredInputValidator<>(Input.body("required", "ok")).validate()).isEmpty();
    }

    @Test
    void nok() {
        assertThat(new RequiredInputValidator<String>(Input.body("required", null)).validate())
                .contains(InputValidationIssues.requiredInput(BODY, "required"));
    }

}
