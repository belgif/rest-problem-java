package io.github.belgif.rest.problem.validation;

import java.util.Optional;

import io.github.belgif.rest.problem.api.InputValidationIssue;

/**
 * Interface for validation of input parameters.
 *
 * <p>
 * Implementors can use {@link AbstractSingleInputValidator} an {@link AbstractMultiInputValidator}.
 * </p>
 */
public interface InputValidator {

    /**
     * Perform the validation.
     *
     * @return an optional containing the {@link InputValidationIssue} value, or empty when the validation succeeds
     */
    Optional<InputValidationIssue> validate();

}
