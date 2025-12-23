package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class IncompleteDateValidatorTest {

    @Test
    void okIncomplete() {
        assertThat(new IncompleteDateValidator(Input.body("test", "2024-01-00")).validate()).isEmpty();
    }

    @Test
    void okComplete() {
        assertThat(new IncompleteDateValidator(Input.body("test", "2024-01-01")).validate()).isEmpty();
    }

    @Test
    void nokMonthOutOfRange() {
        assertThat(new IncompleteDateValidator(Input.body("test", "2024-13-01")).validate())
                .contains(InputValidationIssues.invalidIncompleteDate(BODY, "test", "2024-13-01"));
    }

    @Test
    void nokDateWithoutMonth() {
        assertThat(new IncompleteDateValidator(Input.body("test", "2024-00-04")).validate())
                .contains(InputValidationIssues.invalidIncompleteDate(BODY, "test", "2024-00-04"));
    }

    @Test
    void nokDayOutOfRange() {
        assertThat(new IncompleteDateValidator(Input.body("test", "2024-02-31")).validate())
                .contains(InputValidationIssues.invalidIncompleteDate(BODY, "test", "2024-02-31"));
    }

    @Test
    void nokInvalidLocalDate() {
        assertThat(new IncompleteDateValidator(Input.body("test", "2023-02-29")).validate())
                .contains(InputValidationIssues.invalidIncompleteDate(BODY, "test", "2023-02-29"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "test", "9999-99-99", "2024/01/01" })
    void nokPattern(String value) {
        assertThat(new IncompleteDateValidator(Input.body("test", value)).validate())
                .contains(InputValidationIssues.invalidIncompleteDate(BODY, "test", value));
    }

}
