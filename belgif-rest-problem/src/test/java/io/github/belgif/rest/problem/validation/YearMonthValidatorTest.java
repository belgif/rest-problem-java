package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class YearMonthValidatorTest {

    @Test
    void ok() {
        assertThat(new YearMonthValidator(Input.body("test", "2024-01")).validate()).isEmpty();
    }

    @Test
    void nok() {
        assertThat(new YearMonthValidator(Input.body("criteria.month", "2024-99")).validate())
                .contains(InputValidationIssues.invalidYearMonth(BODY, "criteria.month", "2024-99"));
    }

}
