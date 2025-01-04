package io.github.belgif.rest.problem.it.model;

import javax.validation.constraints.Min;

public class ChildModel extends Model {
    @Min(1)
    private int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "ParentRequestBody{" +
                "email='" + getEmail() + '\'' +
                ", name='" + getName() + '\'' +
                ", age=" + age +
                '}';
    }

}
