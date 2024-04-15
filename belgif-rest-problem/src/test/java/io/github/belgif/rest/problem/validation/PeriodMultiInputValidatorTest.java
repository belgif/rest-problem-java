package io.github.belgif.rest.problem.validation;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Year;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class PeriodMultiInputValidatorTest {

    @Test
    void validateOkLocalDate() {
        PeriodMultiInputValidator<LocalDate> tested = new PeriodMultiInputValidator<>(
                Input.query("startDate", LocalDate.of(2023, 1, 1)),
                Input.query("endDate", LocalDate.of(2023, 12, 31)));
        assertThat(tested.validate()).isEmpty();
    }

    @Test
    void validateOkOffsetDateTime() {
        PeriodMultiInputValidator<OffsetDateTime> tested = new PeriodMultiInputValidator<>(
                Input.query("startTime", OffsetDateTime.now()),
                Input.query("endTime", OffsetDateTime.now().plusHours(1)));
        assertThat(tested.validate()).isEmpty();
    }

    @Test
    void validateOkYear() {
        PeriodMultiInputValidator<Year> tested = new PeriodMultiInputValidator<>(
                Input.query("startYear", Year.of(2023)),
                Input.query("endYear", Year.of(2024)));
        assertThat(tested.validate()).isEmpty();
    }

    @Test
    void validateNullInputs() {
        assertThrows(NullPointerException.class, () -> new PeriodMultiInputValidator<>(null, null),
                "Should have thrown exception");
    }

    @Test
    void validateNOkLocalDate() {
        Input<LocalDate> start = Input.query("startDate", LocalDate.of(2023, 1, 1));
        Input<LocalDate> end = Input.query("endDate", LocalDate.of(2022, 12, 31));
        PeriodMultiInputValidator<LocalDate> tested = new PeriodMultiInputValidator<>(start, end);
        assertThat(tested.validate()).contains(InputValidationIssues.invalidPeriod(start, end));
    }

    @Test
    void validateNOkOffsetDateTime() {
        Input<OffsetDateTime> start = Input.query("startTime", OffsetDateTime.now());
        Input<OffsetDateTime> end = Input.query("endTime", OffsetDateTime.now().minusHours(1));
        PeriodMultiInputValidator<OffsetDateTime> tested = new PeriodMultiInputValidator<>(start, end);
        assertThat(tested.validate()).contains(InputValidationIssues.invalidPeriod(start, end));
    }

    @Test
    void validateNOkYear() {
        Input<Year> start = Input.query("startYear", Year.of(2023));
        Input<Year> end = Input.query("endYear", Year.of(2022));
        PeriodMultiInputValidator<Year> tested = new PeriodMultiInputValidator<>(start, end);
        assertThat(tested.validate()).contains(InputValidationIssues.invalidPeriod(start, end));
    }

}
