package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class EstablishmentUnitNumberValidatorTest {
    private EstablishmentUnitNumberValidator tested;

    @Test
    void validateOk() {
        tested = new EstablishmentUnitNumberValidator(
                Input.body("criteria.establishmentUnitNumbers.establishmentUnitNumber", "2297964444"));
        assertThat(tested.validate()).isEmpty();
    }

    @Test
    void validateNullInput() {
        assertThrows(NullPointerException.class, () -> new EstablishmentUnitNumberValidator(null),
                "Should have thrown exception");
    }

    @Test
    void validateNullIn() {
        assertThrows(NullPointerException.class,
                () -> new EstablishmentUnitNumberValidator(new Input<>(null, "establishmentUnitNumber", "2297964444")),
                "Should have thrown exception");
    }

    @Test
    void validateNullName() {
        assertThrows(NullPointerException.class,
                () -> new EstablishmentUnitNumberValidator(new Input<>(BODY, null, "2297964444")),
                "Should have thrown exception");
    }

    @Test
    void validateNOk() {
        tested = new EstablishmentUnitNumberValidator(
                Input.body("criteria.establishmentUnitNumbers.establishmentUnitNumber", "2111111111"));
        InputValidationIssue expected =
                InputValidationIssues.invalidEstablishmentUnitNumber(BODY,
                        "criteria.establishmentUnitNumbers.establishmentUnitNumber",
                        "2111111111");
        assertThat(tested.validate()).contains(expected);
    }
}
