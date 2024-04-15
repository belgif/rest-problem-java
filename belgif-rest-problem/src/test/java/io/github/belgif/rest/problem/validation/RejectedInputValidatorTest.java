package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class RejectedInputValidatorTest {

    @Test
    void validateOk() {
        RejectedInputValidator<String> validator = new RejectedInputValidator<>(Input.body("reject", null));
        assertDoesNotThrow(validator::validate);
    }

    @Test
    void validateNOk() {
        RejectedInputValidator<String> validator = new RejectedInputValidator<>(Input.body("reject", "bad"));
        InputValidationIssue expected = InputValidationIssues.rejectedInput(BODY, "reject", "bad");
        InputValidationIssue result = validator.validate().get();
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

}
