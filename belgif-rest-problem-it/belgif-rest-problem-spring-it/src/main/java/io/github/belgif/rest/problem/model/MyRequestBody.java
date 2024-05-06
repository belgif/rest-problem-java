package io.github.belgif.rest.problem.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class MyRequestBody {

    @Email
    private String email;
    @NotBlank
    private String name;

    public MyRequestBody() {
        // NoArg constructor for a dataclass only used in an integration test
    }

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
}
