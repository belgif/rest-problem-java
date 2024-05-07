package io.github.belgif.rest.problem.model;

import jakarta.validation.constraints.Min;

public class ChildRequestBody extends ParentRequestBody {
    @Min(1)
    private int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
