package io.github.belgif.rest.problem.validation;

public abstract class AbstractRequestValidatorExtensionB<SELF extends AbstractRequestValidatorExtensionB<SELF>>
        extends AbstractRequestValidatorExtensionA<SELF> {

    public SELF b() {
        return getThis();
    }

}
