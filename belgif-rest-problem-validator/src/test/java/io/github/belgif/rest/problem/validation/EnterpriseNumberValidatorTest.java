package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class EnterpriseNumberValidatorTest {

    @Test
    void ok() {
        assertThat(new EnterpriseNumberValidator(Input.body("test", "0884303369")).validate()).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = { "test", "54321", "20000000032", "2111111111", "0884303370" })
    void nok(String value) {
        assertThat(new EnterpriseNumberValidator(Input.body("test", value)).validate())
                .contains(InputValidationIssues.invalidEnterpriseNumber(BODY, "test", value));
    }

}
