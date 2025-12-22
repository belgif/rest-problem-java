package io.github.belgif.rest.problem.validation;

import java.util.Optional;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;

/**
 * Validates the structure of a Belgif openapi-employment-identifier EmployerId.
 *
 * @see <a href="https://github.com/belgif/openapi-employment-identifier">openapi-employment-identifier</a>
 */
class EmployerIdValidator extends AbstractSingleInputValidator<Long> {

    EmployerIdValidator(Input<Long> input) {
        super(input);
    }

    @Override
    public Optional<InputValidationIssue> validate() {
        if (getValue() < 197L || getValue() > 5999999999L) {
            return Optional.of(InputValidationIssues.invalidEmployerId(getIn(), getName(), getValue()));
        }
        int checksum = (int) (getValue() % 100);
        if (!(checksum == calculateNssoChecksum() || (isInPlaRange() && checksum == calculatePlaChecksum()))) {
            return Optional.of(InputValidationIssues.invalidEmployerId(getIn(), getName(), getValue()));
        }
        return Optional.empty();
    }

    private int calculateNssoChecksum() {
        long radix = getValue() / 100;
        int checksum = (int) (96 - (radix * 100) % 97L);
        return checksum == 0 ? 97 : checksum;
    }

    private int calculatePlaChecksum() {
        long radix = getValue() / 100;
        int checksum = (int) (radix % 97L);
        return checksum == 0 ? 97 : checksum;
    }

    private boolean isInPlaRange() {
        return getValue() >= 197L && getValue() <= 99999926L;
    }

}
