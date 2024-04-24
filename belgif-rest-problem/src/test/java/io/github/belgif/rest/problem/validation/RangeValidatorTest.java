package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class RangeValidatorTest {

    @Test
    void ok() {
        assertThat(new RangeValidator<>(Input.query("page", 3), 1, 5).validate()).isEmpty();
        assertThat(new RangeValidator<>(Input.query("page", 5), 1, 5).validate()).isEmpty();
        assertThat(new RangeValidator<>(Input.query("page", 1), 1, 5).validate()).isEmpty();
        assertThat(new RangeValidator<>(Input.query("page", 3), 1, null).validate()).isEmpty();
        assertThat(new RangeValidator<>(Input.query("page", 1), 1, null).validate()).isEmpty();
        assertThat(new RangeValidator<>(Input.query("page", 3), null, 5).validate()).isEmpty();
        assertThat(new RangeValidator<>(Input.query("page", 5), null, 5).validate()).isEmpty();
    }

    @Test
    void nokNullMinAndMax() {
        assertThatIllegalArgumentException().isThrownBy(() -> new RangeValidator<>(Input.query("page", 3), null, null))
                .withMessage("At least one of min, max must be non-null");
    }

    @Test
    void nokTooSmall() {
        assertThat(new RangeValidator<>(Input.query("page", 0), 1, 5).validate()).contains(
                InputValidationIssues.outOfRange(QUERY, "page", 0, 1, 5));
    }

    @Test
    void nokTooSmallMinOnly() {
        assertThat(new RangeValidator<>(Input.query("page", 0), 1, null).validate()).contains(
                InputValidationIssues.outOfRange(QUERY, "page", 0, 1, null));
    }

    @Test
    void nokTooLarge() {
        assertThat(new RangeValidator<>(Input.query("page", 6), 1, 5).validate()).contains(
                InputValidationIssues.outOfRange(QUERY, "page", 6, 1, 5));
    }

    @Test
    void nokTooLargeMaxOnly() {
        assertThat(new RangeValidator<>(Input.query("page", 6), null, 5).validate()).contains(
                InputValidationIssues.outOfRange(QUERY, "page", 6, null, 5));
    }

}
