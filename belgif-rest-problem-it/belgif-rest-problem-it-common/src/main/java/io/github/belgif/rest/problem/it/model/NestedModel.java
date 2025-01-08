package io.github.belgif.rest.problem.it.model;

import javax.validation.Valid;

public class NestedModel {
    @Valid
    private Model nested;

    public NestedModel(Model nested) {
        this.nested = nested;
    }

    public NestedModel() {
    }

    public Model getNested() {
        return nested;
    }

    public void setNested(Model nested) {
        this.nested = nested;
    }

    @Override
    public String toString() {
        return "NestedModel{" +
                "nested=" + nested +
                '}';
    }

}
