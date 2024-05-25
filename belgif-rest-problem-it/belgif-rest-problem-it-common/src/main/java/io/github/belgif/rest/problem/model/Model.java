package io.github.belgif.rest.problem.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class Model {

    @Email
    private String email;
    @NotBlank
    private String name;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ParentRequestBody{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

}
