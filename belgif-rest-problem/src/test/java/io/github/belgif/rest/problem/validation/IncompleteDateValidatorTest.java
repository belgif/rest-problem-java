package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class IncompleteDateValidatorTest {

    private IncompleteDateValidator tested;

    @Test
    void validateOkIncomplete() {
        tested = new IncompleteDateValidator(Input.body("criteria.date", "2024-01-00"));
        assertThat(tested.validate()).isEmpty();
    }

    @Test
    void validateOkComplete() {
        tested = new IncompleteDateValidator(Input.body("criteria.date", "2024-01-01"));
        assertThat(tested.validate()).isEmpty();
    }

    @Test
    void validateNullInput() {
        assertThrows(NullPointerException.class, () -> new IncompleteDateValidator(null),
                "Should have thrown exception");
    }

    @Test
    void validateNullIn() {
        assertThrows(NullPointerException.class,
                () -> new IncompleteDateValidator(new Input<>(null, "criteria.date", "2024-01-01")),
                "Should have thrown exception");
    }

    @Test
    void validateNullName() {
        assertThrows(NullPointerException.class,
                () -> new IncompleteDateValidator(new Input<>(BODY, null, "2024-01-01")),
                "Should have thrown exception");
    }

    @Test
    void validateNOkMonthPart() {
        tested = new IncompleteDateValidator(Input.body("criteria.date", "2024-13-01"));
        InputValidationIssue expected =
                InputValidationIssues.invalidIncompleteDate(BODY, "criteria.date", "2024-13-01");
        assertThat(tested.validate()).contains(expected);

        tested = new IncompleteDateValidator(Input.body("criteria.date", "2024-00-04"));
        expected =
                InputValidationIssues.invalidIncompleteDate(BODY, "criteria.date", "2024-00-04");
        assertThat(tested.validate()).contains(expected);
    }

    @Test
    void validateNOkDayPart() {
        tested = new IncompleteDateValidator(Input.body("criteria.date", "2024-02-31"));
        InputValidationIssue expected =
                InputValidationIssues.invalidIncompleteDate(BODY, "criteria.date", "2024-02-31");
        assertThat(tested.validate()).contains(expected);
    }

    @Test
    void validateNOkCouldNoCreateLocalDate() {
        tested = new IncompleteDateValidator(Input.body("criteria.date", "2023-02-29"));
        InputValidationIssue expected =
                InputValidationIssues.invalidIncompleteDate(BODY, "criteria.date", "2023-02-29");
        assertThat(tested.validate()).contains(expected);
    }

    @Test
    void validateNOkTemporalCreationError() {
        tested = new IncompleteDateValidator(Input.body("criteria.date", "2024-00-01"));
        InputValidationIssue expected =
                InputValidationIssues.invalidIncompleteDate(BODY, "criteria.date", "2024-00-01");
        assertThat(tested.validate()).contains(expected);
    }
}
