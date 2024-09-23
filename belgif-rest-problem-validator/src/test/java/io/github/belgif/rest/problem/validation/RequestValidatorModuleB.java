package io.github.belgif.rest.problem.validation;

public interface RequestValidatorModuleB<V extends RequestValidatorModuleB<V>> extends RequestValidatorModule<V> {

    default V b() {
        return getThis();
    }

}
