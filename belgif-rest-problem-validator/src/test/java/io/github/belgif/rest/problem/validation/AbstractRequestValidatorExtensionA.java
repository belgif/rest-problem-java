package io.github.belgif.rest.problem.validation;

public abstract class AbstractRequestValidatorExtensionA<V extends AbstractRequestValidatorExtensionA<V>>
        extends AbstractRequestValidator<V> {

    public V a() {
        return getThis();
    }

}
