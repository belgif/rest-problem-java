package io.github.belgif.rest.problem.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration for invalid parameter locations.
 */
public enum InEnum {

    BODY("body"),
    PATH("path"),
    QUERY("query"),
    HEADER("header");

    private final String value;

    InEnum(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }

    @JsonCreator
    public static InEnum fromValue(String value) {
        for (InEnum in : InEnum.values()) {
            if (in.value.equals(value)) {
                return in;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

}
