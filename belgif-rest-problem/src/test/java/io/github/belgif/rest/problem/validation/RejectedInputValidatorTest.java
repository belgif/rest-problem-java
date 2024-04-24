package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class RejectedInputValidatorTest {

    @Test
    void ok() {
        assertThat(new RejectedInputValidator<String>(Input.body("reject", null)).validate()).isEmpty();
    }

    @Test
    void nok() {
        assertThat(new RejectedInputValidator<>(Input.body("reject", "bad")).validate())
                .contains(InputValidationIssues.rejectedInput(BODY, "reject", "bad"));
    }

}
