package io.github.belgif.rest.problem.validation;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Year;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class PeriodMultiInputValidatorTest {

    @Test
    void okLocalDate() {
        PeriodMultiInputValidator<LocalDate> validator = new PeriodMultiInputValidator<>(
                Input.query("startDate", LocalDate.of(2023, 1, 1)),
                Input.query("endDate", LocalDate.of(2023, 12, 31)));
        assertThat(validator.validate()).isEmpty();
    }

    @Test
    void okOffsetDateTime() {
        PeriodMultiInputValidator<OffsetDateTime> validator = new PeriodMultiInputValidator<>(
                Input.query("startTime", OffsetDateTime.now()),
                Input.query("endTime", OffsetDateTime.now().plusHours(1)));
        assertThat(validator.validate()).isEmpty();
    }

    @Test
    void okYear() {
        PeriodMultiInputValidator<Year> validator = new PeriodMultiInputValidator<>(
                Input.query("startYear", Year.of(2023)),
                Input.query("endYear", Year.of(2024)));
        assertThat(validator.validate()).isEmpty();
    }

    @Test
    void nokNullStartInput() {
        assertThatNullPointerException()
                .isThrownBy(() -> new PeriodMultiInputValidator<>(null, Input.query("endDate", LocalDate.now())))
                .withMessage("start should not be null");
    }

    @Test
    void nokNullEndInput() {
        assertThatNullPointerException()
                .isThrownBy(() -> new PeriodMultiInputValidator<>(Input.query("startDate", LocalDate.now()), null))
                .withMessage("end should not be null");
    }

    @Test
    void nokLocalDate() {
        Input<LocalDate> start = Input.query("startDate", LocalDate.of(2023, 1, 1));
        Input<LocalDate> end = Input.query("endDate", LocalDate.of(2022, 12, 31));
        PeriodMultiInputValidator<LocalDate> validator = new PeriodMultiInputValidator<>(start, end);
        assertThat(validator.validate()).contains(InputValidationIssues.invalidPeriod(start, end));
    }

    @Test
    void nokOffsetDateTime() {
        Input<OffsetDateTime> start = Input.query("startTime", OffsetDateTime.now());
        Input<OffsetDateTime> end = Input.query("endTime", OffsetDateTime.now().minusHours(1));
        PeriodMultiInputValidator<OffsetDateTime> validator = new PeriodMultiInputValidator<>(start, end);
        assertThat(validator.validate()).contains(InputValidationIssues.invalidPeriod(start, end));
    }

    @Test
    void nokYear() {
        Input<Year> start = Input.query("startYear", Year.of(2023));
        Input<Year> end = Input.query("endYear", Year.of(2022));
        PeriodMultiInputValidator<Year> validator = new PeriodMultiInputValidator<>(start, end);
        assertThat(validator.validate()).contains(InputValidationIssues.invalidPeriod(start, end));
    }

}
