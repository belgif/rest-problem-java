package io.github.belgif.rest.problem.validation;

import java.util.Optional;
import java.util.regex.Pattern;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

/**
 * Validates the structure of a Belgif openapi-organization-identifier EnterpriseNumber.
 *
 * @see <a href="https://github.com/belgif/openapi-organization-identifier">openapi-organization-identifier</a>
 */
class EnterpriseNumberValidator extends AbstractSingleInputValidator<String> {

    private static final Pattern PATTERN = Pattern.compile("^[0-1]\\d{9}$");

    EnterpriseNumberValidator(Input<String> input) {
        super(input);
    }

    @Override
    public Optional<InputValidationIssue> validate() {
        if (!PATTERN.matcher(getValue()).matches() || !Checksum.validateModulo97Checksum(Long.parseLong(getValue()))) {
            return Optional.of(InputValidationIssues.invalidEnterpriseNumber(getIn(), getName(), getValue()));
        }
        return Optional.empty();
    }
}
