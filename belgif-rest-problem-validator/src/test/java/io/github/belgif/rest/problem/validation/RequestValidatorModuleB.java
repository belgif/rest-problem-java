package io.github.belgif.rest.problem.validation;

public interface RequestValidatorModuleB<THIS extends RequestValidatorModuleB<THIS>>
        extends RequestValidatorModule<THIS> {

    default THIS b() {
        return getThis();
    }

}
