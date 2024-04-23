package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class PeriodValidatorTest {

    @Test
    void ok() {
        // only endDate
        assertThat(new PeriodValidator(Input.body("period", new InputPeriod(null, LocalDate.of(2023, 10, 12))))
                .validate()).isEmpty();
        // only startDate
        assertThat(new PeriodValidator(Input.body("period", new InputPeriod(LocalDate.of(2023, 10, 12), null)))
                .validate()).isEmpty();
        // startDate == endDate
        assertThat(new PeriodValidator(
                Input.body("period", new InputPeriod(LocalDate.of(2023, 10, 12), LocalDate.of(2023, 10, 12))))
                        .validate()).isEmpty();
        // startDate < endDate
        assertThat(new PeriodValidator(
                Input.body("period", new InputPeriod(LocalDate.of(2023, 10, 11), LocalDate.of(2023, 10, 12))))
                        .validate()).isEmpty();
        // no startDate and endDate
        assertThat(new PeriodValidator(Input.body("criteria.periods.period", new InputPeriod(null, null)))
                .validate()).isEmpty();
    }

    @Test
    void nokStartDateAfterEndDate() {
        InputPeriod badPeriod = new InputPeriod(LocalDate.of(2023, 10, 14), LocalDate.of(2023, 10, 12));
        assertThat(new PeriodValidator(Input.body("period", badPeriod)).validate()).contains(
                InputValidationIssues.invalidPeriod(BODY, "period", badPeriod));
    }

    @Test
    void nokInvalidObject() {
        assertThatIllegalArgumentException().isThrownBy(() -> new PeriodValidator(
                Input.body("period", "oops")).validate());
    }

}
