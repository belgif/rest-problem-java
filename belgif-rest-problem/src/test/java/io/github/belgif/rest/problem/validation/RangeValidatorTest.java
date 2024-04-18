package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class RangeValidatorTest {

    private RangeValidator<Integer> tested;

    @Test
    void validateOk() {
        tested = new RangeValidator<>(Input.query("page", 3), 0, 5);
        assertThat(tested.validate()).isEmpty();
        tested = new RangeValidator<>(Input.query("page", 5), 0, 5);
        assertThat(tested.validate()).isEmpty();
        tested = new RangeValidator<>(Input.query("page", 0), 0, 5);
        assertThat(tested.validate()).isEmpty();
        tested = new RangeValidator<>(Input.query("page", 3), 0, null);
        assertThat(tested.validate()).isEmpty();
        tested = new RangeValidator<>(Input.query("page", 0), 0, null);
        assertThat(tested.validate()).isEmpty();
        tested = new RangeValidator<>(Input.query("page", 3), null, 5);
        assertThat(tested.validate()).isEmpty();
        tested = new RangeValidator<>(Input.query("page", 5), null, 5);
        assertThat(tested.validate()).isEmpty();
    }

    @Test
    void validateNullMinAndMax() {
        assertThrows(IllegalArgumentException.class, () -> new RangeValidator<>(Input.query("page", 3), null, null),
                "Should have thrown exception");
    }

    @Test
    void validateNullInput() {
        assertThrows(NullPointerException.class, () -> new RangeValidator<>(null, 0, 5),
                "Should have thrown exception");
    }

    @Test
    void validateNullIn() {
        assertThrows(NullPointerException.class,
                () -> new RangeValidator<>(new Input<>(null, "page", 3), 0, 5),
                "Should have thrown exception");
    }

    @Test
    void validateNullName() {
        assertThrows(NullPointerException.class,
                () -> new RangeValidator<>(new Input<>(QUERY, null, 3), 0, 5),
                "Should have thrown exception");
    }

    @Test
    void validateOutOfRangeTooSmall() {
        tested = new RangeValidator<>(Input.query("page", -1), 0, 5);
        InputValidationIssue expected =
                InputValidationIssues.outOfRange(QUERY, "page", -1, 0, 5);
        assertThat(tested.validate()).contains(expected);
    }

    @Test
    void validateOutOfRangeTooSmallMinOnly() {
        tested = new RangeValidator<>(Input.query("page", -1), 0, null);
        InputValidationIssue expected =
                InputValidationIssues.outOfRange(QUERY, "page", -1, 0, null);
        assertThat(tested.validate()).contains(expected);
    }

    @Test
    void validateOutOfRangeTooLarge() {
        tested = new RangeValidator<>(Input.query("page", 6), 0, 5);
        InputValidationIssue expected =
                InputValidationIssues.outOfRange(QUERY, "page", 6, 0, 5);
        assertThat(tested.validate()).contains(expected);
    }

    @Test
    void validateOutOfRangeTooLargeMaxOnly() {
        tested = new RangeValidator<>(Input.query("page", 6), null, 5);
        InputValidationIssue expected =
                InputValidationIssues.outOfRange(QUERY, "page", 6, null, 5);
        assertThat(tested.validate()).contains(expected);
    }

}
