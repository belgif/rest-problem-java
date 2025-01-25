package io.github.belgif.rest.problem.it.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JacksonModel {

    private Integer id;

    private String description;

    @JsonCreator
    public JacksonModel(@JsonProperty(required = true, value = "id") Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
