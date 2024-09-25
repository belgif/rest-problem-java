package io.github.belgif.rest.problem.validation;

public interface RequestValidatorModuleA<THIS extends RequestValidatorModuleA<THIS>>
        extends RequestValidatorModule<THIS> {

    default THIS a() {
        return getThis();
    }

}
