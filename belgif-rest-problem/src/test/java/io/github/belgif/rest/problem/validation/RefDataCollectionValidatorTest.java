package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class RefDataCollectionValidatorTest {

    private RefDataCollectionValidator<String> tested;

    @Test
    void validateOk() {
        tested = new RefDataCollectionValidator<>(Input.query("refData", "ok"), Collections.singletonList("ok"));
        assertThat(tested.validate()).isEmpty();
    }

    @Test
    void validateNullInput() {
        assertThrows(NullPointerException.class,
                () -> new RefDataCollectionValidator<>(null, Collections.singletonList("ok")),
                "Should have thrown exception");
    }

    @Test
    void validateNullIn() {
        assertThrows(NullPointerException.class,
                () -> new RefDataCollectionValidator<>(new Input<>(null, "refData", "ok"),
                        Collections.singletonList("ok")),
                "Should have thrown exception");
    }

    @Test
    void validateNullName() {
        assertThrows(NullPointerException.class,
                () -> new RefDataCollectionValidator<>(new Input<>(QUERY, null, "ok"), Collections.singletonList("ok")),
                "Should have thrown exception");
    }

    @Test
    void validateNullAllowedRefData() {
        assertThrows(NullPointerException.class,
                () -> new RefDataCollectionValidator<>(new Input<>(QUERY, "refData", "ok"), null),
                "Should have thrown exception");
    }

    @Test
    void validateEmptyAllowedRefData() {
        assertThrows(IllegalStateException.class,
                () -> new RefDataCollectionValidator<>(new Input<>(QUERY, "refData", "ok"), Collections.emptyList()),
                "Should have thrown exception");
    }

    @Test
    void validateNOk() {
        tested = new RefDataCollectionValidator<>(Input.query("refData", "bad"), Collections.singletonList("ok"));
        InputValidationIssue expected = InputValidationIssues.referencedResourceNotFound(QUERY, "refData", "bad");
        assertThat(tested.validate()).contains(expected);
    }
}
