package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class PeriodValidatorTest {
    private PeriodValidator tested;

    @Test
    void validateOk() {

        // only endDate
        tested = new PeriodValidator(
                Input.body("criteria.periods.period", new InputPeriod(null, LocalDate.of(2023, 10, 12))));
        assertThat(tested.validate()).isEmpty();

        // only startDate
        tested = new PeriodValidator(
                Input.body("criteria.periods.period", new InputPeriod(LocalDate.of(2023, 10, 12), null)));
        assertThat(tested.validate()).isEmpty();

        // startDate == endDate
        tested = new PeriodValidator(
                Input.body("criteria.periods.period",
                        new InputPeriod(LocalDate.of(2023, 10, 12), LocalDate.of(2023, 10, 12))));
        assertThat(tested.validate()).isEmpty();

        // startDate < endDate
        tested = new PeriodValidator(
                Input.body("criteria.periods.period",
                        new InputPeriod(LocalDate.of(2023, 10, 11), LocalDate.of(2023, 10, 12))));
        assertThat(tested.validate()).isEmpty();

        // no startDate and endDate
        tested = new PeriodValidator(Input.body("criteria.periods.period", new InputPeriod(null, null)));
        assertThat(tested.validate()).isEmpty();
    }

    @Test
    void validateNullInput() {
        assertThrows(NullPointerException.class, () -> new PeriodValidator(null),
                "Should have thrown exception");
    }

    @Test
    void validateNullIn() {
        assertThrows(NullPointerException.class,
                () -> new PeriodValidator(new Input<>(null, "period",
                        new InputPeriod(null, LocalDate.of(2023, 10, 12)))),
                "Should have thrown exception");
    }

    @Test
    void validateNullName() {
        assertThrows(NullPointerException.class,
                () -> new PeriodValidator(new Input<>(BODY, null,
                        new InputPeriod(null, LocalDate.of(2023, 10, 12)))),
                "Should have thrown exception");
    }

    @Test
    void validateNOkStartDateAfterEndDate() {
        InputPeriod badPeriod = new InputPeriod(LocalDate.of(2023, 10, 14), LocalDate.of(2023, 10, 12));
        tested = new PeriodValidator(Input.body("criteria.periods.period", badPeriod));
        InputValidationIssue expected = InputValidationIssues.invalidPeriod(BODY, "criteria.periods.period", badPeriod);
        assertThat(tested.validate()).contains(expected);
    }

    @Test
    void validateNOkInvalidObject() {
        tested = new PeriodValidator(
                Input.body("criteria.periods.period", new String()));
        assertThrows(IllegalArgumentException.class, () -> tested.validate(), "Should have thrown exception");
    }
}
