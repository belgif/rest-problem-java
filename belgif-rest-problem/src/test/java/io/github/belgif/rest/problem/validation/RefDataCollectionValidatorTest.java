package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class RefDataCollectionValidatorTest {

    @Test
    void ok() {
        assertThat(new RefDataCollectionValidator<>(Input.query("refData", "ok"), Collections.singletonList("ok"))
                .validate()).isEmpty();
    }

    @Test
    void nokNullAllowedRefData() {
        assertThatNullPointerException().isThrownBy(
                () -> new RefDataCollectionValidator<>(new Input<>(QUERY, "refData", "ok"), null));
    }

    @Test
    void nokEmptyAllowedRefData() {
        assertThatIllegalStateException().isThrownBy(
                () -> new RefDataCollectionValidator<>(new Input<>(QUERY, "refData", "ok"), Collections.emptyList()));
    }

    @Test
    void nok() {
        assertThat(new RefDataCollectionValidator<>(Input.query("refData", "bad"), Collections.singletonList("ok"))
                .validate()).contains(InputValidationIssues.referencedResourceNotFound(QUERY, "refData", "bad"));
    }

}
