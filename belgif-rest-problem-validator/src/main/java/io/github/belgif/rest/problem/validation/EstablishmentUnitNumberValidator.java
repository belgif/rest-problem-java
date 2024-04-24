package io.github.belgif.rest.problem.validation;

import java.util.Optional;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

/**
 * Validates the structure of a Belgif openapi-organization-identifier EstablishmentUnitNumber.
 *
 * @see <a href="https://github.com/belgif/openapi-organization-identifier">openapi-organization-identifier</a>
 */
class EstablishmentUnitNumberValidator extends AbstractSingleInputValidator<String> {

    EstablishmentUnitNumberValidator(Input<String> input) {
        super(input);
    }

    @Override
    public Optional<InputValidationIssue> validate() {
        // Belgif openapi-organization-identifier EstablishmentUnitNumber already enforces ^[2-8]\d{9}$ regex:
        // we only need to validate the module 97 checksum here
        if (!Checksum.validateModulo97Checksum(Long.parseLong(getValue()))) {
            return Optional.of(InputValidationIssues.invalidEstablishmentUnitNumber(getIn(), getName(), getValue()));
        }
        return Optional.empty();
    }
}
