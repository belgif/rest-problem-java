package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class EstablishmentUnitNumberValidatorTest {

    @Test
    void ok() {
        assertThat(new EstablishmentUnitNumberValidator(Input.body("test", "2297964444")).validate()).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = { "test", "54321", "2111111111", "2297964445" })
    void nok(String value) {
        assertThat(new EstablishmentUnitNumberValidator(Input.body("test", value)).validate())
                .contains(InputValidationIssues.invalidEstablishmentUnitNumber(BODY, "test", value));
    }

}
