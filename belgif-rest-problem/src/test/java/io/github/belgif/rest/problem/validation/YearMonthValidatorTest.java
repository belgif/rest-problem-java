package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class YearMonthValidatorTest {

    private YearMonthValidator tested;

    @Test
    void validateOk() {
        tested = new YearMonthValidator(Input.body("criteria.month", "2024-01"));
        assertThat(tested.validate()).isEmpty();
    }

    @Test
    void validateNullInput() {
        assertThrows(NullPointerException.class, () -> new YearMonthValidator(null),
                "Should have thrown exception");
    }

    @Test
    void validateNullIn() {
        assertThrows(NullPointerException.class,
                () -> new YearMonthValidator(new Input<>(null, "criteria.month", "1984-07-00")),
                "Should have thrown exception");
    }

    @Test
    void validateNullName() {
        assertThrows(NullPointerException.class,
                () -> new YearMonthValidator(new Input<>(BODY, null, "1984-07-00")),
                "Should have thrown exception");
    }

    @Test
    void validateNOk() {
        tested = new YearMonthValidator(Input.body("criteria.month", "2024-99"));
        InputValidationIssue expected =
                InputValidationIssues.invalidYearMonth(BODY, "criteria.month", "2024-99");
        assertThat(tested.validate()).contains(expected);
    }
}
