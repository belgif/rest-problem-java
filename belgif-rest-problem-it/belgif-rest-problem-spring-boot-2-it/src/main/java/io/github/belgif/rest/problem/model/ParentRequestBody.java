package io.github.belgif.rest.problem.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class ParentRequestBody {

    @Email
    private String email;
    @NotBlank
    private String name;

    public ParentRequestBody() {
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
