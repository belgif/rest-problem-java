package io.github.belgif.rest.problem.validation;

/**
 * Interface for composable request validator modules.
 *
 * @param <THIS> self-referencing RequestValidatorModule type (for extensible fluent builder pattern)
 */
public interface RequestValidatorModule<THIS extends RequestValidatorModule<THIS>> {

    /**
     * Return this self-referencing RequestValidatorModule type (for extensible fluent builder pattern).
     *
     * @return this
     */
    THIS getThis();

    /**
     * Add an input validator.
     *
     * @param validator the InputValidator
     */
    void addValidator(InputValidator validator);

}
