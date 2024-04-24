package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class RefDataPredicateValidatorTest {

    @Test
    void ok() {
        assertThat(new RefDataPredicateValidator<>(Input.query("refData", "ok"), "ok"::equals).validate()).isEmpty();
    }

    @Test
    void nokNullAllowedRefData() {
        assertThatNullPointerException().isThrownBy(
                () -> new RefDataPredicateValidator<>(new Input<>(QUERY, "refData", "ok"), null))
                .withMessage("allowedRefData predicate should not be null");
    }

    @Test
    void validateNOk() {
        assertThat(new RefDataPredicateValidator<>(Input.query("refData", "bad"), "ok"::equals).validate())
                .contains(InputValidationIssues.referencedResourceNotFound(QUERY, "refData", "bad"));
    }

}
