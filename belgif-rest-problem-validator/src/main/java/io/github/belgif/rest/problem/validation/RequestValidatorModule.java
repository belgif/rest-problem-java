package io.github.belgif.rest.problem.validation;

/**
 * Interface for composable request validator modules.
 *
 * @param <V> self-referencing RequestValidatorModule type (for extensible builder pattern)
 */
public interface RequestValidatorModule<V extends RequestValidatorModule<V>> {

    /**
     * Return this self-referencing RequestValidatorModule type (for extensible builder pattern).
     *
     * @return this
     */
    V getThis();

    /**
     * Add an input validator.
     *
     * @param validator the InputValidator
     */
    void addValidator(InputValidator validator);

}
