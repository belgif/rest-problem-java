package io.github.belgif.rest.problem.it.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Size;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

public class Bean {

    @PathParam("name")
    @Size(min = 2, max = 256)
    private String theName;
    @QueryParam("value")
    @Max(5)
    private Integer theValue;

    public String getName() {
        return theName;
    }

    public void setName(String name) {
        this.theName = name;
    }

    public Integer getValue() {
        return theValue;
    }

    public void setValue(Integer value) {
        this.theValue = value;
    }

    @Override
    public String toString() {
        return "Bean{" +
                "name='" + theName + '\'' +
                ", value=" + theValue +
                '}';
    }

}
