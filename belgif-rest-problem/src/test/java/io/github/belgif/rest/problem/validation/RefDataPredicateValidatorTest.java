package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class RefDataPredicateValidatorTest {

    private RefDataPredicateValidator<String> tested;

    @Test
    void validateOk() {
        tested = new RefDataPredicateValidator<>(Input.query("refData", "ok"), "ok"::equals);
        assertThat(tested.validate()).isEmpty();
    }

    @Test
    void validateNullInput() {
        assertThrows(NullPointerException.class,
                () -> new RefDataPredicateValidator<>(null, s -> true),
                "Should have thrown exception");
    }

    @Test
    void validateNullIn() {
        assertThrows(NullPointerException.class,
                () -> new RefDataPredicateValidator<>(new Input<>(null, "refData", "ok"), "ok"::equals),
                "Should have thrown exception");
    }

    @Test
    void validateNullName() {
        assertThrows(NullPointerException.class,
                () -> new RefDataPredicateValidator<>(new Input<>(QUERY, null, "ok"), "ok"::equals),
                "Should have thrown exception");
    }

    @Test
    void validateNullAllowedRefData() {
        assertThrows(NullPointerException.class,
                () -> new RefDataPredicateValidator<>(new Input<>(QUERY, "refData", "ok"), null),
                "Should have thrown exception");
    }

    @Test
    void validateNOk() {
        tested = new RefDataPredicateValidator<>(Input.query("refData", "bad"), "ok"::equals);
        InputValidationIssue expected = InputValidationIssues.referencedResourceNotFound(QUERY, "refData", "bad");
        assertThat(tested.validate()).contains(expected);
    }
}
