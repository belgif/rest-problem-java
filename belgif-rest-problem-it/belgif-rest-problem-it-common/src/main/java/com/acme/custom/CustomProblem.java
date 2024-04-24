package com.acme.custom;

import java.net.URI;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.belgif.rest.problem.api.ClientProblem;
import io.github.belgif.rest.problem.api.ProblemType;

@ProblemType(CustomProblem.TYPE)
public class CustomProblem extends ClientProblem {

    public static final String TYPE = "urn:problem-type:acme:custom";
    public static final URI TYPE_URI = URI.create(TYPE);
    public static final String TITLE = "Custom Problem";
    public static final int STATUS = 409;

    private static final long serialVersionUID = 1L;

    private String customField;

    @JsonCreator
    public CustomProblem(@JsonProperty("customField") String customField) {
        super(TYPE_URI, TITLE, STATUS);
        this.customField = customField;
    }

    public void setCustomField(String customField) {
        this.customField = customField;
    }

    public String getCustomField() {
        return customField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        CustomProblem that = (CustomProblem) o;
        return Objects.equals(customField, that.customField);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), customField);
    }

}
