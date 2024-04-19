package io.github.belgif.rest.problem.validation;

import static io.github.belgif.rest.problem.api.InEnum.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

class RequiredInputValidatorTest {

    @Test
    void validateOk() {
        RequiredInputValidator<String> validator = new RequiredInputValidator<>(Input.body("required", "ok"));
        assertDoesNotThrow(validator::validate);
    }

    @Test
    void validateNOk() {
        RequiredInputValidator<String> validator = new RequiredInputValidator<>(Input.body("required", null));
        InputValidationIssue expected = InputValidationIssues.requiredInput(BODY, "required");
        InputValidationIssue result = validator.validate().get();
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

}
