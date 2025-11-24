package io.github.belgif.rest.problem.it.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Size;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

public class Bean {

    @PathParam("name")
    @Size(min = 2, max = 256)
    private String name;
    @QueryParam("value")
    @Max(5)
    private Integer value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Bean{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }

}
