package io.github.belgif.rest.problem.validation;

public interface RequestValidatorModuleA<V extends RequestValidatorModuleA<V>> extends RequestValidatorModule<V> {

    default V a() {
        return getThis();
    }

}
