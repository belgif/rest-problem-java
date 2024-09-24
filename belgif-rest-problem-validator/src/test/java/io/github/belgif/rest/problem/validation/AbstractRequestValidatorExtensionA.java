package io.github.belgif.rest.problem.validation;

public abstract class AbstractRequestValidatorExtensionA<V extends AbstractRequestValidatorExtensionA<V>>
        extends AbstractRequestValidator<V> {

    public AbstractRequestValidatorExtensionA(Class<V> clazz) {
        super(clazz);
    }

    public V a() {
        return getThis();
    }

}
