package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class EnterpriseNumberValidatorTest {
    private EnterpriseNumberValidator tested;

    @Test
    void validateOk() {
        tested = new EnterpriseNumberValidator(
                Input.body("criteria.enterpriseNumbers.enterpriseNumber", "0884303369"));
        assertThat(tested.validate()).isEmpty();
    }

    @Test
    void validateNullInput() {
        assertThrows(NullPointerException.class, () -> new EnterpriseNumberValidator(null),
                "Should have thrown exception");
    }

    @Test
    void validateNullIn() {
        assertThrows(NullPointerException.class,
                () -> new EnterpriseNumberValidator(new Input<>(null, "enterpriseNumber", "0884303369")),
                "Should have thrown exception");
    }

    @Test
    void validateNullName() {
        assertThrows(NullPointerException.class,
                () -> new EnterpriseNumberValidator(new Input<>(BODY, null, "0884303369")),
                "Should have thrown exception");
    }

    @Test
    void validateNOk() {
        tested = new EnterpriseNumberValidator(
                Input.body("criteria.enterpriseNumbers.enterpriseNumber", "2111111111"));
        InputValidationIssue expected =
                InputValidationIssues.invalidEnterpriseNumber(BODY, "criteria.enterpriseNumbers.enterpriseNumber",
                        "2111111111");
        assertThat(tested.validate()).contains(expected);
    }
}
