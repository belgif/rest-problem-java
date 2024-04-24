package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class EstablishmentUnitNumberValidatorTest {

    @Test
    void ok() {
        assertThat(new EstablishmentUnitNumberValidator(Input.body("test", "2297964444")).validate()).isEmpty();
    }

    @Test
    void nok() {
        assertThat(new EstablishmentUnitNumberValidator(Input.body("test", "2111111111")).validate())
                .contains(InputValidationIssues.invalidEstablishmentUnitNumber(BODY, "test", "2111111111"));
    }

}
