package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class EnterpriseNumberValidatorTest {

    @Test
    void ok() {
        assertThat(new EnterpriseNumberValidator(Input.body("test", "0884303369")).validate()).isEmpty();
    }

    @Test
    void nok() {
        assertThat(new EnterpriseNumberValidator(Input.body("test", "2111111111")).validate())
                .contains(InputValidationIssues.invalidEnterpriseNumber(BODY, "test", "2111111111"));
    }

}
