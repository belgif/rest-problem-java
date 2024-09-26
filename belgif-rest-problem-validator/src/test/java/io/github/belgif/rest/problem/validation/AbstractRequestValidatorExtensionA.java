package io.github.belgif.rest.problem.validation;

public abstract class AbstractRequestValidatorExtensionA<SELF extends AbstractRequestValidatorExtensionA<SELF>>
        extends AbstractRequestValidator<SELF> {

    public SELF a() {
        return getThis();
    }

}
