package io.github.belgif.rest.problem.validation;

public abstract class AbstractRequestValidatorExtensionB<V extends AbstractRequestValidatorExtensionB<V>>
        extends AbstractRequestValidatorExtensionA<V> {

    public V b() {
        return getThis();
    }

}
