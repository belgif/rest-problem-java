package com.acme.custom;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonCreator;

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
    public CustomProblem(String customField) {
        super(TYPE_URI, TITLE, STATUS);
        this.customField = customField;
    }

    public void setCustomField(String customField) {
        this.customField = customField;
    }

    public String getCustomField() {
        return customField;
    }

}
